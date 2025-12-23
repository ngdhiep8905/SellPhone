import { $ } from "../common/dom.js";
import { CartState } from "../common/state.js";
import {
  apiFetchCart,
  apiUpdateCartItem,
  apiRemoveCartItem,
} from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";
import { updateCartHeaderCount } from "../common/header.js";

const SELECTED_KEY = "sellphone_selected_cart_item_ids";
let selectedIds = new Set();

/* ===============================
   Storage helpers
================================ */
function loadSelectedFromStorage() {
  try {
    const raw = sessionStorage.getItem(SELECTED_KEY);
    if (!raw) return;
    const arr = JSON.parse(raw);
    if (Array.isArray(arr)) selectedIds = new Set(arr.map(String));
  } catch {
    selectedIds = new Set();
  }
}

function saveSelectedToStorage() {
  sessionStorage.setItem(SELECTED_KEY, JSON.stringify([...selectedIds]));
}

/* ===============================
   Summary calc (theo item đã tick)
================================ */
function calcSelectedSummary() {
  const items = CartState.cart?.items || [];
  let totalQty = 0;
  let subtotal = 0;

  for (const item of items) {
    const id = String(item.cartItemId);
    if (!selectedIds.has(id)) continue;

    const qty = item.quantity || 0;
    const price = getPhonePrice(item.phone || {});
    totalQty += qty;
    subtotal += qty * price;
  }

  return { totalQty, subtotal };
}

function renderSummaryBox() {
  const countEl = $("#cart-count");
  const subtotalEl = $("#cart-subtotal");
  const totalEl = $("#cart-total");
  const checkoutBtn = $("#go-to-checkout");

  const { totalQty, subtotal } = calcSelectedSummary();

  if (countEl) countEl.textContent = `${totalQty} sản phẩm`;
  if (subtotalEl) subtotalEl.textContent = formatVND(subtotal);
  if (totalEl) totalEl.textContent = formatVND(subtotal);

  if (checkoutBtn) checkoutBtn.disabled = totalQty <= 0;
}

/* ===============================
   Ensure selectedIds hợp lệ
================================ */
function ensureSelectedValid() {
  const items = CartState.cart?.items || [];
  const valid = new Set(items.map((i) => String(i.cartItemId)));

  // drop id không còn tồn tại
  let changed = false;
  for (const id of [...selectedIds]) {
    if (!valid.has(id)) {
      selectedIds.delete(id);
      changed = true;
    }
  }
  if (changed) saveSelectedToStorage();

  // Mặc định: nếu chưa chọn gì thì chọn tất cả cho mượt UX
  if (selectedIds.size === 0 && items.length > 0) {
    items.forEach((i) => selectedIds.add(String(i.cartItemId)));
    saveSelectedToStorage();
  }
}

/* ===============================
   Render cart
================================ */
function renderCartPage() {
  const itemsEl = $("#cart-items");
  const emptyEl = $("#cart-empty");

  if (!itemsEl) return;

  // Cart rỗng
  if (!CartState.cart?.items?.length) {
    itemsEl.innerHTML = "";
    if (emptyEl) emptyEl.classList.remove("sp-hidden");

    selectedIds.clear();
    saveSelectedToStorage();

    renderSummaryBox();
    updateCartHeaderCount();
    return;
  }

  if (emptyEl) emptyEl.classList.add("sp-hidden");

  ensureSelectedValid();

  const items = CartState.cart.items;

  const allSelected =
    items.length > 0 && items.every((i) => selectedIds.has(String(i.cartItemId)));

  // Header chọn tất cả
  itemsEl.innerHTML = `
    <div class="sp-cart-item" style="padding: 12px 14px; align-items:center;">
      <div style="display:flex; align-items:center; gap:10px;">
        <input id="cart-select-all" type="checkbox" ${allSelected ? "checked" : ""} />
        <label for="cart-select-all" style="cursor:pointer; font-weight:600;">Chọn tất cả</label>
      </div>
      <div class="sp-text--muted" style="font-size:12px;">
        Chỉ tính tiền các sản phẩm được chọn.
      </div>
    </div>
  `;

  // List item
  for (const item of items) {
    const phone = item.phone || {};
    const qty = item.quantity || 0;
    const price = getPhonePrice(phone);
    const lineTotal = qty * price;

    const id = String(item.cartItemId);
    const checked = selectedIds.has(id);

    const specsInfo = phone.chipset
      ? `<small class="sp-text--muted">${phone.chipset} | ${phone.ramSize || ""}</small>`
      : "";

    const row = document.createElement("div");
    row.className = "sp-cart-item";
    row.innerHTML = `
      <div style="display:flex; align-items:center; gap:10px;">
        <input class="sp-cart-check" type="checkbox" data-check-id="${id}" ${checked ? "checked" : ""} />
      </div>

      <div class="sp-cart-item__main" style="flex:1;">
        <div class="sp-cart-item__info">
          <span class="sp-cart-item__name">${phone.phoneName || "Điện thoại"}</span>
          ${specsInfo}
        </div>
        <span class="sp-cart-item__price">${formatVND(lineTotal)}</span>
      </div>

      <div class="sp-cart-item__controls">
        <div class="sp-quantity-group">
          <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="minus" data-id="${id}">-</button>
          <span class="sp-quantity-val">${qty}</span>
          <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="plus" data-id="${id}">+</button>
        </div>

        <button class="sp-btn sp-btn--ghost sp-btn--sm" data-cart-action="remove" data-id="${id}">
          <span style="color: red;">Xoá</span>
        </button>
      </div>
    `;
    itemsEl.appendChild(row);
  }

  renderSummaryBox();
  updateCartHeaderCount();
}

/* ===============================
   Page init
================================ */
export function initCartPage() {
  const itemsEl = $("#cart-items");
  const checkoutBtn = $("#go-to-checkout");
  if (!itemsEl || !checkoutBtn) return;

  loadSelectedFromStorage();

  // ✅ Checkbox: dùng change cho chắc
  itemsEl.addEventListener("change", (e) => {
    const t = e.target;

    // Chọn tất cả
    if (t && t.id === "cart-select-all") {
      if (t.checked) {
        (CartState.cart?.items || []).forEach((i) =>
          selectedIds.add(String(i.cartItemId))
        );
      } else {
        selectedIds.clear();
      }
      saveSelectedToStorage();
      renderCartPage();
      return;
    }

    // Tick item
    if (t && t.classList && t.classList.contains("sp-cart-check")) {
      const id = String(t.dataset.checkId || "");
      if (!id) return;

      if (t.checked) selectedIds.add(id);
      else selectedIds.delete(id);

      saveSelectedToStorage();
      renderSummaryBox();
      return;
    }
  });

  // Nút +/- / remove: dùng click
  itemsEl.addEventListener("click", async (e) => {
    const btn = e.target.closest("button[data-cart-action]");
    if (!btn) return;

    const cartItemId = String(btn.dataset.id || "");
    const action = btn.dataset.cartAction;

    const current = CartState.cart?.items?.find(
      (i) => String(i.cartItemId) === cartItemId
    );
    if (!current) return;

    const qty = current.quantity || 0;

    // plus
    if (action === "plus") {
      if (current.phone && qty >= current.phone.stockQuantity) {
        alert(`Sản phẩm này chỉ còn ${current.phone.stockQuantity} chiếc trong kho!`);
        return;
      }
      await apiUpdateCartItem(cartItemId, qty + 1);
      await apiFetchCart();
      renderCartPage();
      return;
    }

    // minus
    if (action === "minus") {
      const newQty = qty - 1;
      if (newQty <= 0) {
        await apiRemoveCartItem(cartItemId);
        selectedIds.delete(cartItemId);
        saveSelectedToStorage();
      } else {
        await apiUpdateCartItem(cartItemId, newQty);
      }
      await apiFetchCart();
      renderCartPage();
      return;
    }

    // remove
    if (action === "remove") {
      if (confirm("Bạn muốn xóa sản phẩm này khỏi giỏ hàng?")) {
        await apiRemoveCartItem(cartItemId);
        selectedIds.delete(cartItemId);
        saveSelectedToStorage();

        await apiFetchCart();
        renderCartPage();
      }
      return;
    }
  });

  // sang checkout: chỉ cho nếu có chọn
  checkoutBtn.onclick = () => {
    const { totalQty } = calcSelectedSummary();
    if (totalQty <= 0) {
      alert("Bạn chưa chọn sản phẩm để thanh toán!");
      return;
    }
    saveSelectedToStorage();
    window.location.href = "checkout.html";
  };

  apiFetchCart().then(renderCartPage);
}
