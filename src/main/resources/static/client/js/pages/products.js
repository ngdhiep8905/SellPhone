import { $ } from "../common/dom.js";
import { ProductsState } from "../common/state.js";
import { apiAddToCart, apiFetchProducts } from "../common/api.js";

// Formatter nội bộ để tránh lỗi do helper không tương thích
function formatVND_SAFE(value) {
  const n = Number(value);
  if (!Number.isFinite(n)) return "Liên hệ";
  return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(n);
}

function getPrice_SAFE(p) {
  // Backend của bạn trả field "price"
  if (p && p.price != null) return Number(p.price);
  // fallback nếu sau này có field khác
  if (p && p.salePrice != null) return Number(p.salePrice);
  return NaN;
}

function applyClientFilters(renderFn) {
  let list = [...(ProductsState.rawItems || [])];

  // Price filter
  if (ProductsState.priceFilter) {
    list = list.filter((p) => {
      const price = getPrice_SAFE(p);
      switch (ProductsState.priceFilter) {
        case "under5":
          return price < 5_000_000;
        case "5to10":
          return price >= 5_000_000 && price <= 10_000_000;
        case "10to20":
          return price > 10_000_000 && price <= 20_000_000;
        case "over20":
          return price > 20_000_000;
        default:
          return true;
      }
    });
  }

  // Sort
  if (ProductsState.sort) {
    const sort = ProductsState.sort;
    list.sort((a, b) => {
      if (sort === "priceAsc") return getPrice_SAFE(a) - getPrice_SAFE(b);
      if (sort === "priceDesc") return getPrice_SAFE(b) - getPrice_SAFE(a);
      if (sort === "nameAsc") return (a.phoneName || "").localeCompare(b.phoneName || "");
      return 0;
    });
  }

  ProductsState.items = list;
  ProductsState.currentPage = 1;
  renderFn();
}

function renderProducts() {
  const container = $("#product-list");
  if (!container) {
    console.error("[products] Missing #product-list");
    return;
  }

  const items = ProductsState.items || [];
  container.innerHTML = "";

  console.log("[products] renderProducts items =", items.length, items);

  if (!items.length) {
    container.innerHTML = "<p>Không tìm thấy sản phẩm nào.</p>";
    return;
  }

  const start = (ProductsState.currentPage - 1) * ProductsState.pageSize;
  const pageItems = items.slice(start, start + ProductsState.pageSize);

  pageItems.forEach((p) => {
    const card = document.createElement("article");
    card.className = "sp-product-card";

    // FIX: backend trả key là phoneId (P001...), không phải id
    const phoneId = p.phoneId;
    if (!phoneId) {
      console.warn("[products] Missing phoneId in product:", p);
      return;
    }

    const price = getPrice_SAFE(p);

    // Vì bạn đang chạy ở http://localhost:8080 nên "/img/..." là đúng
    const imgSrc = p.coverImageURL || "placeholder.jpg";

    const pid = encodeURIComponent(phoneId);

    card.innerHTML = `
      <a href="product-detail.html?id=${pid}" class="sp-product-card__wrapper" style="text-decoration: none; color: inherit; display: block;">
        <div class="sp-product-card__image">
          <img src="${imgSrc}" alt="${p.phoneName || ""}">
          ${p.stockQuantity <= 0 ? '<div class="out-of-stock-label">Hết hàng</div>' : ""}
        </div>
        <div class="sp-product-card__title">${p.phoneName || ""}</div>
        <div class="sp-product-card__specs">
          <span class="badge-item">${p.chipset || ""}</span>
          <span class="badge-item">${p.ramSize || ""}</span>
        </div>
        <div class="sp-product-card__price">${formatVND_SAFE(price)}</div>
      </a>

      <div class="sp-product-card__footer">
        <button
          class="sp-btn sp-btn--primary sp-btn--sm"
          data-id="${pid}"
          ${p.stockQuantity <= 0 ? "disabled" : ""}
        >
          Thêm vào giỏ
        </button>
        <a href="product-detail.html?id=${pid}" class="sp-btn sp-btn--outline sp-btn--sm">Chi tiết</a>
      </div>
    `;

    container.appendChild(card);
  });
}


async function applyProductFilters() {
  const newKeyword = $("#search-input")?.value.trim() || "";
  const newBrandId = $("#brand-filter")?.value || "";

  ProductsState.keyword = newKeyword;
  ProductsState.brandId = newBrandId;
  ProductsState.priceFilter = $("#price-filter")?.value || "";
  ProductsState.sort = $("#sort-filter")?.value || "";

  console.log("[products] applyProductFilters", {
    keyword: newKeyword,
    brandId: newBrandId,
    priceFilter: ProductsState.priceFilter,
    sort: ProductsState.sort,
  });

  await apiFetchProducts(newKeyword, newBrandId);
  applyClientFilters(renderProducts);
}

export function initProductsPage() {
  const listEl = $("#product-list");
  if (!listEl) {
    console.error("[products] initProductsPage: Missing #product-list");
    return;
  }

  console.log("[products] initProductsPage called");

  // Lắng nghe filter
  ["search-input", "brand-filter", "price-filter", "sort-filter"].forEach((id) => {
    const el = $(`#${id}`);
    if (!el) return;
    el.addEventListener("change", applyProductFilters);
    el.addEventListener("input", applyProductFilters);
  });

  // Delegation add-to-cart + thông báo thành công/thất bại
  listEl.addEventListener("click", async (e) => {
    const addBtn = e.target.closest("button[data-id]");
    if (!addBtn) return;

    e.preventDefault();
    e.stopPropagation();

    const raw = addBtn.dataset.id;
    const phoneId = raw ? decodeURIComponent(raw) : "";

    if (!phoneId) {
      alert("Không lấy được mã sản phẩm để thêm vào giỏ.");
      return;
    }

    // Tránh double click
    const oldText = addBtn.textContent;
    addBtn.disabled = true;
    addBtn.textContent = "Đang thêm...";

    try {
      await apiAddToCart(phoneId, 1);

      alert("Đã thêm vào giỏ hàng!");
      // Nếu bạn có header count thì cập nhật luôn (nếu hàm này đã import sẵn)
      // updateCartHeaderCount?.();

    } catch (err) {
      console.error("[products] add to cart failed:", err);
      alert("Thêm vào giỏ thất bại. Vui lòng thử lại.");
    } finally {
      // Trả lại trạng thái nút (nếu còn hàng)
      addBtn.disabled = false;
      addBtn.textContent = oldText;
    }
  });


  // Load lần đầu (CÓ catch + hiển thị lỗi)
  apiFetchProducts()
    .then(() => applyClientFilters(renderProducts))
    .catch((err) => {
      console.error("[products] apiFetchProducts failed:", err);
      listEl.innerHTML =
        "<p>Không tải được sản phẩm. Mở DevTools (F12) để xem lỗi Network/Console.</p>";
    });
}
