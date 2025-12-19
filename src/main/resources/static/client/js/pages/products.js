import { $, } from "../common/dom.js";
import { AppState, ProductsState } from "../common/state.js";
import { apiAddToCart, apiFetchCart, apiFetchProducts } from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";
import { updateCartHeaderCount } from "../common/header.js";

function applyClientFilters(renderProducts) {
  let list = [...(ProductsState.rawItems || [])];
  if (ProductsState.priceFilter) {
    list = list.filter((p) => {
      const price = getPhonePrice(p);
      switch (ProductsState.priceFilter) {
        case "under5": return price < 5_000_000;
        case "5to10": return price >= 5_000_000 && price <= 10_000_000;
        case "10to20": return price > 10_000_000 && price <= 20_000_000;
        case "over20": return price > 20_000_000;
        default: return true;
      }
    });
  }
  if (ProductsState.sort) {
    const sort = ProductsState.sort;
    list.sort((a, b) => {
      if (sort === "priceAsc") return getPhonePrice(a) - getPhonePrice(b);
      if (sort === "priceDesc") return getPhonePrice(b) - getPhonePrice(a);
      if (sort === "nameAsc") return (a.phoneName || "").localeCompare(b.phoneName || "");
      return 0;
    });
  }
  ProductsState.items = list;
  ProductsState.currentPage = 1;
  renderProducts();
}

function renderProducts() {
  const container = $("#product-list");
  if (!container) return;
  container.innerHTML = "";

  if (!ProductsState.items.length) {
    container.innerHTML = "<p>Không tìm thấy sản phẩm nào.</p>";
    return;
  }

  const start = (ProductsState.currentPage - 1) * ProductsState.pageSize;
  const pageItems = ProductsState.items.slice(start, start + ProductsState.pageSize);

  pageItems.forEach((p) => {
      const card = document.createElement("article");
      card.className = "sp-product-card";

      const phoneId = p.phoneId || p.id;
      const price = getPhonePrice(p);

      // BỌC TOÀN BỘ NỘI DUNG TRONG THẺ <a>
      // Lưu ý: href phải trỏ đúng đến file product-detail.html
      card.innerHTML = `
        <a href="product-detail.html?id=${phoneId}" class="sp-product-card__wrapper" style="text-decoration: none; color: inherit; display: block;">
          <div class="sp-product-card__image">
            <img src="${p.coverImageURL || 'placeholder.jpg'}" alt="${p.phoneName}">
            ${p.stockQuantity <= 0 ? '<div class="out-of-stock-label">Hết hàng</div>' : ''}
          </div>
          <div class="sp-product-card__title">${p.phoneName}</div>
          <div class="sp-product-card__specs">
            <span class="badge-item">${p.chipset || ''}</span>
            <span class="badge-item">${p.ramSize || ''}</span>
          </div>
          <div class="sp-product-card__price">${formatVND(price)}</div>
        </a>

        <div class="sp-product-card__footer">
          <button class="sp-btn sp-btn--primary sp-btn--sm"
                  onclick="event.preventDefault(); event.stopPropagation(); apiAddToCart('${phoneId}', 1)">
            Thêm vào giỏ
          </button>
          <a href="product-detail.html?id=${phoneId}" class="sp-btn sp-btn--outline sp-btn--sm">Chi tiết</a>
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

  await apiFetchProducts(newKeyword, newBrandId);
  applyClientFilters(renderProducts);
}

export function initProductsPage() {
  const listEl = $("#product-list");
  if (!listEl) return;

  // 1. Lắng nghe sự kiện lọc (giữ nguyên)
  ["search-input", "brand-filter", "price-filter", "sort-filter"].forEach(id => {
    $(`#${id}`)?.addEventListener("change", applyProductFilters);
    $(`#${id}`)?.addEventListener("input", applyProductFilters);
  });

  // 2. SỬA LẠI ĐOẠN NÀY: Xử lý click linh hoạt
  listEl.addEventListener("click", (e) => {
    // Nếu click trúng nút "Thêm vào giỏ" (hoặc icon bên trong nút)
    const addBtn = e.target.closest("button.sp-btn--primary");
    if (addBtn) {
        // Chỉ nút này mới chặn chuyển trang để thực hiện logic Add To Cart
        e.preventDefault();
        e.stopPropagation();
        const id = addBtn.dataset.id;
        apiAddToCart(id, 1);
        return; // Thoát ra, không làm gì thêm
    }

    // Nếu không phải nút Add to cart, hãy để trình duyệt tự xử lý thẻ <a>
    // KHÔNG dùng e.preventDefault() ở đây thì nó sẽ "nhảy vèo" ngay.
  });

  apiFetchProducts().then(() => applyClientFilters(renderProducts));
}