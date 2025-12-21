import { $ } from "../common/dom.js";
import { AppState } from "../common/state.js";
import { apiLogin, apiFetchCart } from "../common/api.js";
import { saveUser } from "../common/auth.js";
import { updateHeaderUI } from "../common/header.js";
import { showToast, getQueryParam } from "../common/helpers.js";



export function initLoginPage() {
  const emailInput = $("#login-email");
  const pwdInput = $("#login-password");
  const submitBtn = $("#login-submit");
  const cancelBtn = $("#login-cancel");
  const errorEl = $("#login-error");
  if (!emailInput || !pwdInput || !submitBtn) return;

  const redirect = getQueryParam("redirect");

  async function handleLogin() {
    const email = emailInput.value.trim();
    const password = pwdInput.value.trim();
    if (!email || !password) {
      errorEl.textContent = "Vui lòng nhập đầy đủ email và mật khẩu.";
      errorEl.classList.remove("sp-hidden");
      return;
    }
    errorEl.classList.add("sp-hidden");

    try {
      const user = await apiLogin(email, password);

      if (user.role?.roleName?.toUpperCase() === "ADMIN") {
        window.location.href = "admin.html";
        return;
      }

      AppState.currentUser = user;
      saveUser();
      // ✅ refresh cart theo cookie mới (giỏ user)
      await apiFetchCart();
      updateHeaderUI();
      showToast("Đăng nhập thành công.");
      window.location.href = redirect || "products.html";
    } catch (err) {
      errorEl.textContent = `Đăng nhập thất bại: ${err.message}.`;
      errorEl.classList.remove("sp-hidden");
    }
  }

  submitBtn.onclick = handleLogin;
  pwdInput.addEventListener("keypress", (e) => e.key === "Enter" && handleLogin());
  if (cancelBtn) cancelBtn.onclick = () => (window.location.href = "index.html");
}
