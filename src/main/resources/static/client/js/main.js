import { loadUser } from "./common/auth.js";
import { updateHeaderUI, updateCartHeaderCount } from "./common/header.js";

import { initLoginPage } from "./pages/login.js";
import { initRegisterPage } from "./pages/register.js";
import { initProductsPage } from "./pages/products.js";
import { initCartPage } from "./pages/cart.js";
import { initProductDetailPage } from "./pages/product-detail.js";
import {initCheckoutPage} from "./pages/checkout.js";
import { initAccountPage } from "./pages/account.js";
import { initHomePage } from "./pages/home.js";


document.addEventListener("DOMContentLoaded", () => {
  loadUser();
  updateHeaderUI();
  updateCartHeaderCount();

  const page = document.body.dataset.page;

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
});
