import { $ } from "../common/dom.js";
import { AppState, CartState, PaymentState } from "../common/state.js";
import { requireLogin } from "../common/auth.js";
import {
  apiFetchCart, apiUpdateCartItem, apiRemoveCartItem, apiCheckout,
  apiFetchPayments
} from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";
import { updateCartHeaderCount } from "../common/header.js";

function renderPaymentOptions() {
  const sel = $("#checkout-payment");
  if (!sel) return;
  sel.innerHTML = "";

  (PaymentState.methods || []).forEach((p) => {
    const id = p.paymentId ?? p.id ?? p.payment_id;
    const name = p.paymentMethod ?? p.method ?? "Thanh toán";
    const opt = document.createElement("option");
    opt.value = String(id);
    opt.textContent = name;
    sel.appendChild(opt);
  });
}

function renderCartPage() {
  const itemsEl = $("#cart-items");
  const countEl = $("#cart-count");
  const subtotalEl = $("#cart-subtotal");
  const shippingEl = $("#cart-shipping");
  const totalEl = $("#cart-total");
  const emptyEl = $("#cart-empty");
  if (!itemsEl) return;

  if (!CartState.cart?.items?.length) {
    itemsEl.innerHTML = "";
    emptyEl && emptyEl.classList.remove("sp-hidden");
    countEl && (countEl.textContent = "0 sp");
    subtotalEl && (subtotalEl.textContent = "0₫");
    shippingEl && (shippingEl.textContent = "0₫");
    totalEl && (totalEl.textContent = "0₫");
    return;
  }

  emptyEl && emptyEl.classList.add("sp-hidden");
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
    row.innerHTML = `
      <div class="sp-cart-item__row">
        <span>${phone.phoneName || "Điện thoại"}</span>
        <span>${formatVND(lineTotal)}</span>
      </div>
      <div class="sp-cart-item__row">
        <small>${phone.phoneDescription || ""}</small>
        <div class="sp-cart-item__controls">
          <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="minus" data-id="${item.cartItemId}">-</button>
          <span>${qty}</span>
          <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="plus" data-id="${item.cartItemId}">+</button>
          <button class="sp-btn sp-btn--ghost sp-btn--sm" data-cart-action="remove" data-id="${item.cartItemId}">X</button>
        </div>
      </div>
    `;
    itemsEl.appendChild(row);
  });

  const shipping = subtotal > 0 ? 30000 : 0;
  const total = subtotal + shipping;

  countEl && (countEl.textContent = `${totalQty} sp`);
  subtotalEl && (subtotalEl.textContent = formatVND(subtotal));
  shippingEl && (shippingEl.textContent = formatVND(shipping));
  totalEl && (totalEl.textContent = formatVND(total));
}

export function initCartPage() {
  if (!requireLogin("cart.html")) {
    updateCartHeaderCount();
    return;
  }

  const itemsEl = $("#cart-items");
  const checkoutBtn = $("#checkout-submit");
  const errorEl = $("#checkout-error");
  const successEl = $("#checkout-success");
  const paymentSelect = $("#checkout-payment");
  if (!itemsEl || !checkoutBtn) return;

  if (paymentSelect) {
    apiFetchPayments()
      .then(() => {
        renderPaymentOptions();
        if (paymentSelect.options.length > 0) paymentSelect.selectedIndex = 0;
      })
      .catch(() => {
        errorEl.textContent = "Không tải được phương thức thanh toán. Kiểm tra API /api/payments.";
        errorEl.classList.remove("sp-hidden");
      });
  }

  itemsEl.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-cart-action]");
    if (!btn) return;

    const cartItemId = btn.dataset.id; // giữ string
    const action = btn.dataset.cartAction;

    const current = CartState.cart?.items?.find(
      (i) => String(i.cartItemId) === String(cartItemId)
    );

    if (!current) return;

    const qty = current.quantity || 0;

    if (action === "plus") apiUpdateCartItem(cartItemId, qty + 1).then(renderCartPage);
    if (action === "minus") {
      const newQty = qty - 1;
      if (newQty <= 0) apiRemoveCartItem(cartItemId).then(renderCartPage);
      else apiUpdateCartItem(cartItemId, newQty).then(renderCartPage);
    }
    if (action === "remove") apiRemoveCartItem(cartItemId).then(renderCartPage);
  });

  checkoutBtn.onclick = async () => {
    const name = $("#checkout-name").value.trim();
    const addr = $("#checkout-address").value.trim();
    const phone = $("#checkout-phone").value.trim();
    const coupon = $("#checkout-coupon").value.trim();

    errorEl.classList.add("sp-hidden");
    successEl.classList.add("sp-hidden");

    if (!CartState.cart?.items?.length) {
      errorEl.textContent = "Giỏ hàng đang trống.";
      errorEl.classList.remove("sp-hidden");
      return;
    }
    if (!name || !addr || !phone) {
      errorEl.textContent = "Vui lòng nhập đầy đủ tên, địa chỉ và số điện thoại.";
      errorEl.classList.remove("sp-hidden");
      return;
    }

    try {
      const selectedPaymentId = paymentSelect ? paymentSelect.value : "";
      if (!selectedPaymentId) {
        errorEl.textContent = "Vui lòng chọn phương thức thanh toán.";
        errorEl.classList.remove("sp-hidden");
        return;
      }

      const order = await apiCheckout({
        userId: AppState.currentUser.userId,
        receiverName: name,
        receiverAddress: addr,
        receiverPhone: phone,
        couponCode: coupon,
        paymentId: selectedPaymentId,
      });

      successEl.textContent = "Đặt hàng thành công! Mã đơn hàng: " + order.orderId;
      successEl.classList.remove("sp-hidden");

      CartState.cart = null;
      updateCartHeaderCount();
      renderCartPage();
    } catch (err) {
      errorEl.textContent = `Thanh toán thất bại: ${err.message}`;
      errorEl.classList.remove("sp-hidden");
    }
  };

  apiFetchCart().then(renderCartPage);
}
