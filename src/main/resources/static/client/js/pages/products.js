import { $, } from "../common/dom.js";
import { AppState, ProductsState } from "../common/state.js";
import { apiAddToCart, apiFetchCart, apiFetchProducts } from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";
import { updateCartHeaderCount } from "../common/header.js";

// Client filters
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
  const countEl = $("#product-count");
  const pagEl = $("#pagination");
  if (!container) return;

  container.innerHTML = "";
  if (countEl) countEl.textContent = "";
  if (pagEl) pagEl.innerHTML = "";

  if (!ProductsState.items.length) {
    container.innerHTML = "<p class='sp-text--muted'>Không tìm thấy sản phẩm nào với bộ lọc hiện tại.</p>";
    return;
  }

  const totalPages = Math.ceil(ProductsState.items.length / ProductsState.pageSize) || 1;
  if (ProductsState.currentPage > totalPages) ProductsState.currentPage = totalPages;

  const start = (ProductsState.currentPage - 1) * ProductsState.pageSize;
  const pageItems = ProductsState.items.slice(start, start + ProductsState.pageSize);

  if (countEl) countEl.textContent = `Hiển thị ${pageItems.length}/${ProductsState.items.length} sản phẩm`;

  pageItems.forEach((p) => {
    const card = document.createElement("article");
    card.className = "sp-product-card";
    const price = getPhonePrice(p);
    const imgHtml = p.coverImageURL ? `<img src="${p.coverImageURL}" alt="${p.phoneName}">` : "Hình minh hoạ";
    const desc = p.phoneDescription || "Chưa có mô tả.";
    const phoneId = p.phoneId || p.id;

    card.innerHTML = `
      <div class="sp-product-card__image">${imgHtml}</div>
      <div class="sp-product-card__title">${p.phoneName}</div>
      <div class="sp-product-card__price">${formatVND(price)}</div>
      <div class="sp-product-card__desc">${desc}</div>
      <div class="sp-product-card__footer">
        <button class="sp-btn sp-btn--primary sp-btn--sm" data-action="add" data-id="${phoneId}">Thêm vào giỏ</button>
        <button class="sp-btn sp-btn--outline sp-btn--sm" data-action="detail" data-id="${phoneId}">Xem chi tiết</button>
      </div>
    `;
    container.appendChild(card);
  });

  if (pagEl && totalPages > 1) {
    pagEl.innerHTML = `
      <button class="sp-page-btn" id="page-prev" ${ProductsState.currentPage === 1 ? "disabled" : ""}>←</button>
      <span>Trang ${ProductsState.currentPage} / ${totalPages}</span>
      <button class="sp-page-btn" id="page-next" ${ProductsState.currentPage === totalPages ? "disabled" : ""}>→</button>
    `;
    $("#page-prev").onclick = () => { if (ProductsState.currentPage > 1) { ProductsState.currentPage--; renderProducts(); } };
    $("#page-next").onclick = () => { if (ProductsState.currentPage < totalPages) { ProductsState.currentPage++; renderProducts(); } };
  }
}

// Detail modal
function openDetailModal(phoneId) {
  const modal = $("#detail-modal");
  if (!modal) return;

  const phone = ProductsState.rawItems.find((p) => p.phoneId === phoneId || p.id === phoneId);
  if (!phone) return;

  AppState.currentDetailPhone = phone;
  $("#detail-name").textContent = phone.phoneName || "";
  $("#detail-price").textContent = formatVND(getPhonePrice(phone));
  $("#detail-desc").textContent = phone.phoneDescription || "Chưa có mô tả chi tiết.";
  $("#detail-brand").textContent = phone.brand?.brandName || "Không rõ";
  $("#detail-storage").textContent = phone.storage || "Không rõ";
  modal.classList.remove("sp-hidden");
}

function closeDetailModal() {
  const modal = $("#detail-modal");
  if (!modal) return;
  AppState.currentDetailPhone = null;
  modal.classList.add("sp-hidden");
}

async function applyProductFilters() {
  const newKeyword = $("#search-input") ? $("#search-input").value.trim() : "";
  const newBrandId = $("#brand-filter") ? $("#brand-filter").value : "";
  const newPriceFilter = $("#price-filter") ? $("#price-filter").value : "";
  const newSort = $("#sort-filter") ? $("#sort-filter").value : "";

  const serverFilterChanged = ProductsState.keyword !== newKeyword || ProductsState.brandId !== newBrandId;

  ProductsState.keyword = newKeyword;
  ProductsState.brandId = newBrandId;
  ProductsState.priceFilter = newPriceFilter;
  ProductsState.sort = newSort;

  if (serverFilterChanged) {
    await apiFetchProducts(newKeyword, newBrandId);
    applyClientFilters(renderProducts);
    return;
  }

  applyClientFilters(renderProducts);
}

export function initProductsPage() {
  const searchInput = $("#search-input");
  const clearBtn = $("#clear-search-btn");
  const brandSelect = $("#brand-filter");
  const priceSelect = $("#price-filter");
  const sortSelect = $("#sort-filter");
  const listEl = $("#product-list");
  const detailCloseEls = document.querySelectorAll('[data-close-modal="detail"]');
  const detailAddBtn = $("#detail-add");
  if (!listEl) return;

  [searchInput, brandSelect, priceSelect, sortSelect].forEach((el) => {
    if (!el) return;
    el.addEventListener(el.tagName === "SELECT" ? "change" : "input", applyProductFilters);
  });

  if (clearBtn) clearBtn.onclick = () => { if (searchInput) searchInput.value = ""; applyProductFilters(); };

  listEl.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-action]");
    if (!btn) return;
    const id = btn.dataset.id;
    const action = btn.dataset.action;
    if (action === "add") apiAddToCart(id, 1);
    if (action === "detail") openDetailModal(id);
  });

  detailCloseEls.forEach((el) => el.addEventListener("click", closeDetailModal));
  if (detailAddBtn) {
    detailAddBtn.onclick = () => {
      if (AppState.currentDetailPhone) {
        apiAddToCart(AppState.currentDetailPhone.phoneId, 1);
        closeDetailModal();
      }
    };
  }

  // initial load
  apiFetchProducts().then(() => applyClientFilters(renderProducts));

  if (AppState.currentUser) apiFetchCart();
  else updateCartHeaderCount();
}
