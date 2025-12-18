import { AppState } from "../common/state.js";
import { apiFetchCart } from "../common/api.js";
import { updateCartHeaderCount } from "../common/header.js";

export function initHomePage() {
  if (AppState.currentUser) apiFetchCart();
  else updateCartHeaderCount();
}
