import { $ } from "../common/dom.js";
import { AppState, CartState } from "../common/state.js";
import { requireLogin } from "../common/auth.js";
import {
  apiFetchCart, apiUpdateCartItem, apiRemoveCartItem
} from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";
import { updateCartHeaderCount } from "../common/header.js";

function renderCartPage() {
  const itemsEl = $("#cart-items");
  const countEl = $("#cart-count");
  const subtotalEl = $("#cart-subtotal");
  const totalEl = $("#cart-total");
  const emptyEl = $("#cart-empty");
  const checkoutBtn = $("#go-to-checkout");

  if (!itemsEl) return;

  // 1. Kiểm tra giỏ hàng trống
  if (!CartState.cart?.items?.length) {
    itemsEl.innerHTML = "";
    if (emptyEl) emptyEl.classList.remove("sp-hidden");
    if (countEl) countEl.textContent = "0 sản phẩm";
    if (subtotalEl) subtotalEl.textContent = "0₫";
    if (totalEl) totalEl.textContent = "0₫";
    if (checkoutBtn) checkoutBtn.disabled = true; // Vô hiệu hóa nút nếu giỏ hàng trống
    return;
  }

  // 2. Render danh sách sản phẩm
  if (emptyEl) emptyEl.classList.add("sp-hidden");
  if (checkoutBtn) checkoutBtn.disabled = false;
  itemsEl.innerHTML = "";

  let totalQty = 0;
  let subtotal = 0;

  CartState.cart.items.forEach((item) => {
    const phone = item.phone || {};
    const qty = item.quantity || 0;
    const price = getPhonePrice(phone);
    const lineTotal = qty * price;

    totalQty += qty;
    subtotal += lineTotal;

    const row = document.createElement("div");
    row.className = "sp-cart-item";

    // Hiển thị thêm thông số Chip/RAM ở giỏ hàng cho chuyên nghiệp
    const specsInfo = phone.chipset ? `<small class="sp-text--muted">${phone.chipset} | ${phone.ramSize}</small>` : "";

    row.innerHTML = `
      <div class="sp-cart-item__main">
        <div class="sp-cart-item__info">
            <span class="sp-cart-item__name">${phone.phoneName || "Điện thoại"}</span>
            ${specsInfo}
        </div>
        <span class="sp-cart-item__price">${formatVND(lineTotal)}</span>
      </div>
      <div class="sp-cart-item__controls">
          <div class="sp-quantity-group">
            <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="minus" data-id="${item.cartItemId}">-</button>
            <span class="sp-quantity-val">${qty}</span>
            <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="plus" data-id="${item.cartItemId}">+</button>
          </div>
          <button class="sp-btn sp-btn--ghost sp-btn--sm" data-cart-action="remove" data-id="${item.cartItemId}">
            <span style="color: red;">Xoá</span>
          </button>
      </div>
    `;
    itemsEl.appendChild(row);
  });

  // 3. Cập nhật tổng tiền (Không tính phí ship ở đây, để dành cho trang checkout)
  if (countEl) countEl.textContent = `${totalQty} sản phẩm`;
  if (subtotalEl) subtotalEl.textContent = formatVND(subtotal);
  if (totalEl) totalEl.textContent = formatVND(subtotal);
}

export function initCartPage() {


  const itemsEl = $("#cart-items");
  const checkoutBtn = $("#go-to-checkout"); // Nút mới chuyển sang checkout.html

  if (!itemsEl || !checkoutBtn) return;

  // Xử lý tăng/giảm số lượng và xóa
  itemsEl.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-cart-action]");
    if (!btn) return;

    const cartItemId = btn.dataset.id;
    const action = btn.dataset.cartAction;

    const current = CartState.cart?.items?.find(
      (i) => String(i.cartItemId) === String(cartItemId)
    );

    if (!current) return;

    const qty = current.quantity || 0;

    if (action === "plus") {
        // Kiểm tra tồn kho trước khi tăng (Ghi điểm đồ án)
        if (current.phone && qty >= current.phone.stockQuantity) {
            alert(`Sản phẩm này chỉ còn ${current.phone.stockQuantity} chiếc trong kho!`);
            return;
        }
        apiUpdateCartItem(cartItemId, qty + 1).then(renderCartPage);
    }

    if (action === "minus") {
      const newQty = qty - 1;
      if (newQty <= 0) apiRemoveCartItem(cartItemId).then(renderCartPage);
      else apiUpdateCartItem(cartItemId, newQty).then(renderCartPage);
    }

    if (action === "remove") {
        if(confirm("Bạn muốn xóa sản phẩm này khỏi giỏ hàng?")) {
            apiRemoveCartItem(cartItemId).then(renderCartPage);
        }
    }
  });

  // CHUYỂN TRANG: Sang trang thanh toán
  checkoutBtn.onclick = () => {
    if (!CartState.cart?.items?.length) {
      alert("Giỏ hàng của bạn đang trống!");
      return;
    }
    // Chuyển hướng người dùng sang trang checkout.html
    window.location.href = "checkout.html";
  };


  apiFetchCart().then(renderCartPage);
}