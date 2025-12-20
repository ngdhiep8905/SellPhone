import { $ } from "./dom.js";
import { AppState, CartState } from "./state.js";
import { saveUser } from "./auth.js";
import { showToast } from "./helpers.js";

export function updateCartHeaderCount() {
  console.log("🛒 updateCartHeaderCount() được gọi");
  console.log("📦 CartState.cart:", CartState.cart);

  const headerCountEl = $("#cart-header-count");
  if (!headerCountEl) {
    console.log("❌ Không tìm thấy #cart-header-count");
    return;
  }

  if (!CartState.cart || !CartState.cart.items) {
    console.log("⚠️ Cart rỗng hoặc chưa load");
    headerCountEl.textContent = "0";
    return;
  }

  let totalQty = 0;
  CartState.cart.items.forEach((item) => {
    console.log("  📱 Item:", item.phoneName, "- Qty:", item.quantity);
    totalQty += item.quantity || 0;
  });

  console.log("✅ Tổng số lượng:", totalQty);
  headerCountEl.textContent = totalQty;
}

export function updateHeaderUI() {
  console.log("🎨 updateHeaderUI() được gọi");
  console.log("👤 AppState.currentUser:", AppState.currentUser);

  const label = $("#user-label");
  const loginBtn = $("#login-header-btn");
  const logoutBtn = $("#logout-header-btn");

  // Kiểm tra xem các element có tồn tại không
  if (!label || !loginBtn || !logoutBtn) {
    console.log("❌ Không tìm thấy elements cần thiết");
    return;
  }

  if (AppState.currentUser) {
    const displayName = AppState.currentUser.fullName || AppState.currentUser.email || "Người dùng";
    label.textContent = `Xin chào, ${displayName}`;
    loginBtn.classList.add("sp-hidden");
    logoutBtn.classList.remove("sp-hidden");
  } else {
    label.textContent = "Chưa đăng nhập";
    loginBtn.classList.remove("sp-hidden");
    logoutBtn.classList.add("sp-hidden");
  }

  loginBtn.onclick = () => (window.location.href = "login.html");
  logoutBtn.onclick = () => {
    AppState.currentUser = null;
    CartState.cart = null;
    saveUser();
    updateHeaderUI();
    updateCartHeaderCount();
    showToast("Đã đăng xuất.");
    if (["cart", "account", "checkout"].includes(document.body.dataset.page)) {
      window.location.href = "index.html";
    }
  };
}