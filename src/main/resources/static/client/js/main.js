import { loadUser } from "./common/auth.js";
import { updateHeaderUI, updateCartHeaderCount } from "./common/header.js";
import { apiFetchCart } from "./common/api.js";
import { AppState } from "./common/state.js";

import { initLoginPage } from "./pages/login.js";
import { initRegisterPage } from "./pages/register.js";
import { initProductsPage } from "./pages/products.js";
import { initCartPage } from "./pages/cart.js";
import { initProductDetailPage } from "./pages/product-detail.js";
import { initCheckoutPage } from "./pages/checkout.js";
import { initAccountPage } from "./pages/account.js";
import { initHomePage } from "./pages/home.js";

async function initApp() {
  console.log("🚀 Initializing app...");

  loadUser();
  console.log("✅ User loaded:", AppState.currentUser);

  updateHeaderUI();
  console.log("✅ Header updated");

  if (AppState.currentUser) {
    try {
      await apiFetchCart();
      console.log("✅ Cart loaded from backend");
    } catch (err) {
      console.error("❌ Failed to load cart:", err);
      updateCartHeaderCount();
    }
  } else {
    updateCartHeaderCount();
  }

  const page = document.body.dataset.page;
  console.log("📄 Current page:", page);

  switch (page) {
    case "login":
      initLoginPage();
      break;
    case "register":
      initRegisterPage();
      break;
    case "products":
      initProductsPage();
      break;
    case "product-detail":
      initProductDetailPage();
      break;
    case "cart":
      initCartPage();
      break;
    case "checkout":
      initCheckoutPage();
      break;
    case "account":
      initAccountPage();
      break;
    case "home":
    default:
      initHomePage();
      break;
  }

  console.log("✅ Page initialized");
}

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initApp);
} else {
  initApp();
}
