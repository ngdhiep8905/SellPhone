import { API_BASE_URL } from "./config.js";
import { AppState, CartState, PaymentState, ProductsState } from "./state.js";
import { requireLogin } from "./auth.js";
import { showToast } from "./helpers.js";
import { updateCartHeaderCount } from "./header.js";

export async function apiFetchPayments() {
  const res = await fetch(`${API_BASE_URL}/api/payments`);
  if (!res.ok) throw new Error("Fetch payments failed");
  PaymentState.methods = (await res.json()) || [];
  return PaymentState.methods;
}

export async function apiLogin(email, password) {
  const params = new URLSearchParams({ email, password });
  const res = await fetch(`${API_BASE_URL}/api/auth/login?${params.toString()}`, { method: "POST" });
  if (!res.ok) throw new Error("Sai email hoặc mật khẩu.");
  return res.json();
}

export async function apiRegister(payload) {
  const res = await fetch(`${API_BASE_URL}/api/users/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  if (!res.ok) throw new Error("Register failed");
  return res.json();
}

// Server filter: keyword/brandId
export async function apiFetchProducts(keyword = "", brandId = "") {
  const params = new URLSearchParams();
  if (keyword) params.append("keyword", keyword);
  if (brandId) params.append("brandId", brandId);
  const qs = params.toString() ? `?${params.toString()}` : "";

  const res = await fetch(`${API_BASE_URL}/api/phones${qs}`);
  if (!res.ok) throw new Error("HTTP " + res.status);

  const fetchedItems = (await res.json()) || [];
  ProductsState.rawItems = fetchedItems;
  return fetchedItems;
}

export async function apiAddToCart(phoneId, quantity = 1) {
  if (!requireLogin("products.html")) return;

  const params = new URLSearchParams({
    userId: AppState.currentUser.userId,
    phoneId,
    quantity,
  });

  const res = await fetch(`${API_BASE_URL}/api/cart/items?${params.toString()}`, { method: "POST" });
  if (!res.ok) throw new Error("HTTP " + res.status);

  CartState.cart = await res.json();
  updateCartHeaderCount();
  showToast("Đã thêm vào giỏ hàng. Xem chi tiết tại trang Giỏ hàng.");
  return CartState.cart;
}

export async function apiFetchCart() {
  if (!AppState.currentUser) {
    CartState.cart = null;
    updateCartHeaderCount();
    return null;
  }
  const res = await fetch(`${API_BASE_URL}/api/cart/${AppState.currentUser.userId}`);
  if (!res.ok) throw new Error("HTTP " + res.status);
  CartState.cart = await res.json();
  updateCartHeaderCount();
  return CartState.cart;
}

export async function apiUpdateCartItem(cartItemId, newQuantity) {
  const res = await fetch(`${API_BASE_URL}/api/cart/items/${cartItemId}?quantity=${newQuantity}`, {
    method: "PUT",
  });
  if (!res.ok) throw new Error("HTTP " + res.status);
  CartState.cart = await res.json();
  updateCartHeaderCount();
  return CartState.cart;
}

export async function apiRemoveCartItem(cartItemId) {
  const res = await fetch(`${API_BASE_URL}/api/cart/items/${cartItemId}`, { method: "DELETE" });
  if (!res.ok) throw new Error("HTTP " + res.status);
  CartState.cart = await res.json();
  updateCartHeaderCount();
  return CartState.cart;
}

export async function apiCheckout(payload) {
  const { userId, receiverName, receiverAddress, receiverPhone, couponCode, paymentId } = payload;

  const params = new URLSearchParams({
    userId,
    receiverName,
    receiverAddress,
    receiverPhone,
    couponCode: couponCode || "",
    paymentId: String(paymentId || ""),
  });

  const res = await fetch(`${API_BASE_URL}/api/orders/checkout?${params.toString()}`, { method: "POST" });
  if (!res.ok) throw new Error("Checkout failed");
  return res.json();
}
