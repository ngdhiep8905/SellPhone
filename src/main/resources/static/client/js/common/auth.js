import { AppState, CartState } from "./state.js";
import { apiLogout, apiFetchCart } from "./api.js";



export function loadUser() {
  console.log("🔍 loadUser() được gọi");
  try {
    const raw = localStorage.getItem("sellphone_user");
    console.log("📦 Raw data from localStorage:", raw);

    if (!raw) {
      console.log("❌ Không có dữ liệu trong localStorage");
      return;
    }

    const u = JSON.parse(raw);
    console.log("✅ Parsed user:", u);

    if (u && u.userId) {
      AppState.currentUser = u;
      console.log("✅ AppState.currentUser đã được set:", AppState.currentUser);
    } else {
      console.log("❌ User data không hợp lệ (thiếu userId)");
    }
  } catch (err) {
    console.error("❌ Lỗi khi load user:", err);
  }
}

export function saveUser() {
  if (AppState.currentUser) {
    localStorage.setItem("sellphone_user", JSON.stringify(AppState.currentUser));
  } else {
    localStorage.removeItem("sellphone_user");
  }
}

export function requireLogin(redirectPage) {
  if (!AppState.currentUser) {
    const target =
      redirectPage || window.location.pathname.replace(/^\//, "") || "index.html";
    window.location.href = "login.html?redirect=" + encodeURIComponent(target);
    return false;
  }
  return true;
}

export function clearSession() {
  AppState.currentUser = null;
  CartState.cart = null;
  saveUser();
}
export async function handleLogout() {
  try {
    await apiLogout();
  } catch (e) {
    console.warn("apiLogout failed:", e?.message || e);
  }

  clearSession();

  try {
    await apiFetchCart(); // load cart guest mới vào CartState
  } catch (e) {
    console.warn("apiFetchCart after logout failed:", e?.message || e);
  }

  window.location.href = "index.html";
}


