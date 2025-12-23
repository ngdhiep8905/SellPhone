import { $ } from "../common/dom.js";
import { AppState, CartState } from "../common/state.js";
import { apiFetchCart, apiCheckout } from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";

/* ===============================
   CHẶN FORM SUBMIT TRUYỀN THỐNG
================================ */
const SELECTED_KEY = "sellphone_selected_cart_item_ids";
function getSelectedIds() {
  try {
    const arr = JSON.parse(sessionStorage.getItem(SELECTED_KEY) || "[]");
    return Array.isArray(arr) ? arr.map(String) : [];
  } catch {
    return [];
  }
}

function blockFormSubmit() {
  const form = document.querySelector("form");
  if (!form) return;

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    e.stopPropagation();
    console.log("🚫 Form submit bị chặn hoàn toàn");
  });
}

/* ===============================
   ĐỊA CHỈ 2 CẤP
================================ */
async function initAddress2Levels() {
  const provinceSel = $("#provinces");
  const wardSel = $("#wards");
  if (!provinceSel || !wardSel) return;

  try {
    const resP = await fetch("https://provinces.open-api.vn/api/v2/");
    const provinces = await resP.json();

    provinceSel.innerHTML =
      '<option value="">Chọn Tỉnh/Thành phố</option>' +
      provinces
        .slice()
        .sort((a, b) => a.name.localeCompare(b.name, "vi"))
        .map((p) => `<option value="${p.code}">${p.name}</option>`)
        .join("");

    provinceSel.onchange = async () => {
      const pCode = provinceSel.value;
      if (!pCode) {
        wardSel.innerHTML = '<option value="">Chọn Xã/Phường</option>';
        return;
      }

      wardSel.innerHTML = '<option value="">Đang tải...</option>';

      try {
        const resDetail = await fetch(
          `https://provinces.open-api.vn/api/v2/p/${pCode}?depth=2`
        );
        if (!resDetail.ok) throw new Error();

        const detail = await resDetail.json();
        const wards = (detail.wards || [])
          .map((w) => w.name)
          .sort((a, b) => a.localeCompare(b, "vi"));

        wardSel.innerHTML =
          '<option value="">Chọn Xã / Phường</option>' +
          wards.map((name) => `<option value="${name}">${name}</option>`).join("");
      } catch {
        wardSel.innerHTML = '<option value="">Không tải được xã/phường</option>';
      }
    };
  } catch (e) {
    console.error("❌ Lỗi API địa chỉ:", e);
  }
}

/* ===============================
   HIỂN THỊ TÓM TẮT GIỎ HÀNG
================================ */
function renderSummary() {
  const listEl = $("#checkout-items-list");
  if (!listEl) return;
  if (!CartState.cart?.items) return;

  const selectedIds = new Set(getSelectedIds());
  const items = CartState.cart.items.filter((i) => selectedIds.has(String(i.cartItemId)));

  if (!items.length) {
    listEl.innerHTML = `<p class="sp-text--muted">Bạn chưa chọn sản phẩm để thanh toán. Vui lòng quay lại giỏ hàng.</p>`;
    const subtotalEl = $("#checkout-subtotal");
    const totalEl = $("#checkout-total");
    if (subtotalEl) subtotalEl.textContent = "0₫";
    if (totalEl) totalEl.textContent = "0₫";
    return;
  }

  listEl.innerHTML = items
    .map(
      (item) => `
      <div style="padding:10px 0;border-bottom:1px dashed #eee">
        <span>${item.phone.phoneName} (x${item.quantity})</span>
        <strong>${formatVND(getPhonePrice(item.phone) * item.quantity)}</strong>
      </div>
    `
    )
    .join("");

  const subtotal = items.reduce(
    (sum, item) => sum + getPhonePrice(item.phone) * item.quantity,
    0
  );

  const subtotalEl = $("#checkout-subtotal");
  const totalEl = $("#checkout-total");

  if (subtotalEl) subtotalEl.textContent = formatVND(subtotal);
  if (totalEl) totalEl.textContent = formatVND(subtotal + 30000);
}


/* ===============================
   TRANG CHECKOUT (EXPORT CHO main.js)
================================ */
export function initCheckoutPage() {
  blockFormSubmit();
  initAddress2Levels();

  // Nếu có login thì prefill cho tiện, không bắt buộc
  if (AppState.currentUser) {
    const nameEl = $("#checkout-name");
    const phoneEl = $("#checkout-phone");
    if (nameEl && !nameEl.value) nameEl.value = AppState.currentUser.fullName || "";
    if (phoneEl && !phoneEl.value) phoneEl.value = AppState.currentUser.phone || "";
  }

  // Token-based cart (không cần login)
  apiFetchCart().then(renderSummary);

  const confirmBtn = $("#confirm-order-btn");
  if (!confirmBtn) return;

  confirmBtn.addEventListener("click", async (e) => {
    e.preventDefault();
    e.stopPropagation();

    // Giỏ trống thì chặn
    if (!CartState.cart?.items?.length) {
      alert("Giỏ hàng của bạn đang trống!");
      return;
    }

    const name = $("#checkout-name")?.value.trim() || "";
    const phone = $("#checkout-phone")?.value.trim() || "";
    const street = $("#checkout-street")?.value.trim() || "";

    const provincesEl = $("#provinces");
    const wardsEl = $("#wards");

    const provinceText = provincesEl?.options[provincesEl.selectedIndex]?.text || "";
    const provinceCode = provincesEl?.value || "";
    const wardText = wardsEl?.value || "";

    const paymentMethod = $("#checkout-payment-method")?.value || "";

    if (!name || !phone || !street || !provinceCode || !wardText) {
      alert("Vui lòng điền đầy đủ thông tin bắt buộc");
      return;
    }

    // Validate phone basic
    if (!/^\d{10}$/.test(phone)) {
      alert("Số điện thoại phải gồm 10 chữ số");
      return;
    }

    // ✅ Payload mới: không cần userId
    // Bạn map theo backend mới (khuyến nghị):
   const selected = getSelectedIds();
   if (!selected.length) {
     alert("Bạn chưa chọn sản phẩm để thanh toán!");
     return;
   }

   const payload = {
     fullName: name,
     phone: phone,
     address: `${street}, ${wardText}, ${provinceText}`,
     paymentMethodId: paymentMethod,
     couponCode: "",
     cartItemIds: selected, // ✅ thêm
   };




    console.log("📦 Payload checkout (guest):", payload);

    try {
      confirmBtn.disabled = true;
      confirmBtn.textContent = "ĐANG XỬ LÝ...";

      const result = await apiCheckout(payload);

      sessionStorage.removeItem(SELECTED_KEY);

      await apiFetchCart();

      // Backend trả: { order: OrdersDTO, checkoutUrl: string|null }
      const orderId = result?.order?.orderId;

      if (!orderId) {
        throw new Error("Không nhận được mã đơn hàng từ server.");
      }

      // Option A: redirect PayOS hosted checkout page
      if (result.checkoutUrl) {
        window.location.href = result.checkoutUrl;
        return;
      }

      // COD hoặc không có link PayOS
      window.location.href = `order-success.html?orderId=${encodeURIComponent(orderId)}`;


    } catch (err) {
      alert("Lỗi đặt hàng: " + err.message);
      confirmBtn.disabled = false;
      confirmBtn.textContent = "XÁC NHẬN ĐẶT HÀNG";
    }

  });
}
