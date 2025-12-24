import { $ } from "./dom.js";
import { AppState, CartState } from "./state.js";
import { handleLogout } from "./auth.js";

export function updateCartHeaderCount() {
  const headerCountEl = $("#cart-header-count");
  if (!headerCountEl) return;

  if (!CartState.cart || !CartState.cart.items) {
    headerCountEl.textContent = "0";
    return;
  }

  let totalQty = 0;
  CartState.cart.items.forEach((item) => {
    totalQty += item.quantity || 0;
  });

  headerCountEl.textContent = totalQty;
}

export function updateHeaderUI() {
  const label = $("#user-label");
  const loginBtn = $("#login-header-btn");
  const logoutBtn = $("#logout-header-btn");

  if (!label || !loginBtn || !logoutBtn) return;

  if (AppState.currentUser) {
    const displayName =
      AppState.currentUser.fullName ||
      AppState.currentUser.email ||
      "Người dùng";

    label.textContent = `Xin chào, ${displayName}`;
    loginBtn.classList.add("sp-hidden");
    logoutBtn.classList.remove("sp-hidden");
  } else {
    label.textContent = "Chưa đăng nhập";
    loginBtn.classList.remove("sp-hidden");
    logoutBtn.classList.add("sp-hidden");
  }

  loginBtn.onclick = () => (window.location.href = "login.html");

  logoutBtn.onclick = handleLogout;
}
