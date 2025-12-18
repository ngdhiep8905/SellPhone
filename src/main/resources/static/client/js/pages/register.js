import { $ } from "../common/dom.js";
import { AppState } from "../common/state.js";
import { apiRegister } from "../common/api.js";
import { saveUser } from "../common/auth.js";
import { updateHeaderUI } from "../common/header.js";
import { showToast } from "../common/helpers.js";

export function initRegisterPage() {
  const fullNameInput = $("#reg-fullname");
  const emailInput = $("#reg-email");
  const phoneInput = $("#reg-phone");
  const addrInput = $("#reg-address");
  const pwdInput = $("#reg-password");
  const submitBtn = $("#reg-submit");
  const cancelBtn = $("#reg-cancel");
  const errorEl = $("#reg-error");
  const successEl = $("#reg-success");

  if (!emailInput || !pwdInput || !submitBtn) return;

  // ===== helpers =====
  const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  const phoneRegex = /^\d{9,15}$/; // 9-15 digits

  function clearState() {
    errorEl?.classList.add("sp-hidden");
    successEl?.classList.add("sp-hidden");
    if (errorEl) errorEl.textContent = "";
    if (successEl) successEl.textContent = "";

    [fullNameInput, emailInput, phoneInput, addrInput, pwdInput]
      .filter(Boolean)
      .forEach((el) => el.classList.remove("sp-input--error"));
  }

  function markError(inputEl, message) {
    if (inputEl) inputEl.classList.add("sp-input--error");
    if (errorEl) {
      errorEl.textContent = message;
      errorEl.classList.remove("sp-hidden");
    }
  }

  // Map backend error -> UI message + field
  function handleBackendError(err) {
    const code = err?.message || "";
    const detail = err?.detail; // nếu apiRegister forward detail

    // Nếu backend đã trả detail thì ưu tiên hiển thị detail
    if (detail) {
      // cố gắng suy ra field từ code
      if (code === "INVALID_EMAIL") emailInput?.classList.add("sp-input--error");
      if (code === "WEAK_PASSWORD") pwdInput?.classList.add("sp-input--error");
      if (code === "PHONE_REQUIRED" || code === "INVALID_PHONE") phoneInput?.classList.add("sp-input--error");
      if (code === "FULL_NAME_REQUIRED") fullNameInput?.classList.add("sp-input--error");

      markError(null, detail);
      return;
    }

    // Fallback theo code
    switch (code) {
      case "EMAIL_EXISTS":
        markError(emailInput, "Email đã tồn tại. Vui lòng dùng email khác.");
        break;
      case "INVALID_EMAIL":
        markError(emailInput, "Email không đúng định dạng. Ví dụ: abc@gmail.com");
        break;
      case "WEAK_PASSWORD":
        markError(pwdInput, "Mật khẩu phải có ít nhất 6 ký tự.");
        break;
      case "PHONE_REQUIRED":
        markError(phoneInput, "Vui lòng nhập số điện thoại.");
        break;
      case "INVALID_PHONE":
        markError(phoneInput, "Số điện thoại chỉ được chứa chữ số (9-15 số).");
        break;
      case "FULL_NAME_REQUIRED":
        markError(fullNameInput, "Vui lòng nhập họ tên.");
        break;
      default:
        markError(null, "Đăng ký thất bại. Vui lòng thử lại.");
        break;
    }
  }

  async function handleRegister() {
    clearState();

    const fullName = (fullNameInput?.value || "").trim();
    const email = (emailInput?.value || "").trim();
    const phone = (phoneInput?.value || "").trim();
    const address = (addrInput?.value || "").trim();
    const password = (pwdInput?.value || "").trim();

    // ===== Frontend validation =====
    if (!fullName) {
      markError(fullNameInput, "Vui lòng nhập họ tên.");
      return;
    }

    if (!email) {
      markError(emailInput, "Vui lòng nhập Email.");
      return;
    }

    if (!emailRegex.test(email)) {
      markError(emailInput, "Email không đúng định dạng. Ví dụ: abc@gmail.com");
      return;
    }

    if (!phone) {
      markError(phoneInput, "Vui lòng nhập số điện thoại.");
      return;
    }

    if (!phoneRegex.test(phone)) {
      markError(phoneInput, "Số điện thoại chỉ được chứa chữ số (9-15 số).");
      return;
    }

    if (!password) {
      markError(pwdInput, "Vui lòng nhập mật khẩu.");
      return;
    }

    if (password.length < 6) {
      markError(pwdInput, "Mật khẩu phải có ít nhất 6 ký tự.");
      return;
    }

    // ===== Call API =====
    try {
      const res = await apiRegister({ fullName, email, phone, address, password });

      // Backend mới có thể trả {message, userId, email, fullName} thay vì full entity
      // Nếu apiRegister trả về entity Users thì vẫn ok.
      AppState.currentUser = res;
      saveUser();
      updateHeaderUI();

      showToast("Đăng ký thành công. Đã đăng nhập.");
      if (successEl) {
        successEl.textContent = "Đăng ký thành công!";
        successEl.classList.remove("sp-hidden");
      }
      window.location.href = "products.html";
    } catch (err) {
      handleBackendError(err);
    }
  }

  submitBtn.onclick = handleRegister;

  // Enter submit (ở password hoặc email đều ok)
  [pwdInput, emailInput, phoneInput, fullNameInput].filter(Boolean).forEach((el) => {
    el.addEventListener("keypress", (e) => e.key === "Enter" && handleRegister());
  });

  if (cancelBtn) cancelBtn.onclick = () => (window.location.href = "login.html");
}
