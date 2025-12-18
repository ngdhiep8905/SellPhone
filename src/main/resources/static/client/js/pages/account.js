import { $ } from "../common/dom.js";
import { AppState } from "../common/state.js";
import { apiFetchCart } from "../common/api.js";
import { updateCartHeaderCount } from "../common/header.js";

export function initAccountPage() {
  const guestEl = $("#account-guest");
  const userEl = $("#account-user");
  if (!guestEl || !userEl) return;

  if (!AppState.currentUser) {
    guestEl.classList.remove("sp-hidden");
    userEl.classList.add("sp-hidden");
    updateCartHeaderCount();
    return;
  }

  guestEl.classList.add("sp-hidden");
  userEl.classList.remove("sp-hidden");

  $("#account-name").textContent = AppState.currentUser.fullName || AppState.currentUser.email || "(chưa có)";
  $("#account-email").textContent = AppState.currentUser.email || "(chưa có)";
  $("#account-phone").textContent = AppState.currentUser.phone || "(chưa có)";
  $("#account-address").textContent = AppState.currentUser.address || "(chưa có)";

  apiFetchCart();
}
