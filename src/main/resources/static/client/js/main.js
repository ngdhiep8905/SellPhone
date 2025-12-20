import { loadUser } from "./common/auth.js";
import { updateHeaderUI, updateCartHeaderCount } from "./common/header.js";
import { apiFetchCart } from "./common/api.js"; // Thêm import này
import { AppState } from "./common/state.js"; // Thêm import này

import { initLoginPage } from "./pages/login.js";
import { initRegisterPage } from "./pages/register.js";
import { initProductsPage } from "./pages/products.js";
import { initCartPage } from "./pages/cart.js";
import { initProductDetailPage } from "./pages/product-detail.js";
import { initCheckoutPage } from "./pages/checkout.js";
import { initAccountPage } from "./pages/account.js";
import { initHomePage } from "./pages/home.js";

// Hàm khởi tạo chính
async function initApp() {
  console.log("🚀 Initializing app...");

  // Bước 1: Load user từ localStorage
  loadUser();
  console.log("✅ User loaded:", AppState.currentUser);

  // Bước 2: Update UI header
  updateHeaderUI();
  console.log("✅ Header updated");

  // Bước 3: Load cart từ backend NẾU đã đăng nhập
  if (AppState.currentUser) {
    try {
      await apiFetchCart(); // apiFetchCart() sẽ tự gọi updateCartHeaderCount()
      console.log("✅ Cart loaded from backend");
    } catch (err) {
      console.error("❌ Failed to load cart:", err);
      updateCartHeaderCount(); // Fallback: hiển thị 0
    }
  } else {
    updateCartHeaderCount(); // Chưa đăng nhập -> hiển thị 0
  }

  // Bước 4: Init page tương ứng
  const page = document.body.dataset.page;
  console.log("📄 Current page:", page);

  switch (page) {
    case "login": initLoginPage(); break;
    case "register": initRegisterPage(); break;
    case "products": initProductsPage(); break;
    case "product-detail": initProductDetailPage(); break;
    case "cart": initCartPage(); break;
    case "checkout": initCheckoutPage(); break;
    case "account": initAccountPage(); break;
    case "home":
    default: initHomePage(); break;
  }

  console.log("✅ Page initialized");
}

// Kiểm tra xem DOM đã sẵn sàng chưa
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initApp);
} else {
  initApp();
}