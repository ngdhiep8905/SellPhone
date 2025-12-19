import { $ } from "./dom.js";
import { AppState, CartState } from "./state.js";
import { saveUser } from "./auth.js";
import { showToast } from "./helpers.js";

export function updateCartHeaderCount() {
  const headerCountEl = $("#cart-header-count");
  if (!headerCountEl) return;

  let totalQty = 0;
  if (CartState.cart && CartState.cart.items) {
    CartState.cart.items.forEach((item) => (totalQty += item.quantity || 0));
  }
  headerCountEl.textContent = totalQty;
}

export function updateHeaderUI() {
  const container = $("#header-container");
  if (!container) return;

  // CHỈ VẼ KHUNG NẾU TRANG ĐÓ ĐANG TRỐNG (Trang chi tiết sẽ rơi vào đây)
  // Nếu là trang Cart đã có sẵn HTML, nó sẽ bỏ qua bước này, không bị reset nữa
  if (container.innerHTML.trim() === "") {
    container.innerHTML = `
      <header class="sp-header">
          <div class="sp-header__left">
              <div class="sp-logo">
                  <span class="sp-logo__icon">📱</span>
                  <div class="sp-logo__text">
                      <span class="sp-logo__brand">SellPhone</span>
                      <span class="sp-logo__tagline">Trải nghiệm đỉnh cao</span>
                  </div>
              </div>
              <nav class="sp-nav">
                  <a href="index.html" class="sp-nav__item">Trang chủ</a>
                  <a href="products.html" class="sp-nav__item">Sản phẩm</a>
              </nav>
          </div>
          <div class="sp-header__right">
              <div class="sp-header__icons">
                  <a href="cart.html" class="sp-header-icon" title="Giỏ hàng">
                      <span class="sp-header-icon__badge" id="cart-header-count">0</span>🛒
                  </a>
                  <a href="account.html" class="sp-header-icon" title="Tài khoản của tôi">👤</a>
              </div>
              <div class="sp-user">
                  <div class="sp-user__info">
                      <span id="user-label" class="sp-user__label">Đang tải...</span>
                  </div>
                  <div class="sp-user__actions">
                      <button id="login-header-btn" class="sp-btn sp-btn--outline sp-btn--sm">Đăng nhập</button>
                      <button id="logout-header-btn" class="sp-btn sp-btn--outline sp-btn--sm sp-hidden">Đăng xuất</button>
                  </div>
              </div>
          </div>
      </header>`;
  }

  // CẬP NHẬT NỘI DUNG (Dù trang cũ hay trang mới vẽ đều chạy đoạn này)
  const label = $("#user-label");
  const loginBtn = $("#login-header-btn");
  const logoutBtn = $("#logout-header-btn");

  if (!label || !loginBtn || !logoutBtn) return;

  // Kiểm tra user từ AppState
  if (AppState.currentUser) {
    const displayName = AppState.currentUser.fullName || AppState.currentUser.email;
    label.textContent = `Xin chào, ${displayName}`;
    loginBtn.classList.add("sp-hidden");
    logoutBtn.classList.remove("sp-hidden");
  } else {
    label.textContent = "Chưa đăng nhập";
    loginBtn.classList.remove("sp-hidden");
    logoutBtn.classList.add("sp-hidden");
  }

  // Gắn sự kiện (Dùng onclick để đảm bảo không bị lặp event listener)
  loginBtn.onclick = () => (window.location.href = "login.html");
  logoutBtn.onclick = () => {
    AppState.currentUser = null;
    CartState.cart = null;
    saveUser();
    window.location.href = "index.html";
  };
}