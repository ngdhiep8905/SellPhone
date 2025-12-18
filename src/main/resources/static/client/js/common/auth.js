import { AppState, CartState } from "./state.js";

export function loadUser() {
  try {
    const raw = localStorage.getItem("sellphone_user");
    if (!raw) return;
    const u = JSON.parse(raw);
    if (u && u.userId) AppState.currentUser = u;
  } catch {}
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
