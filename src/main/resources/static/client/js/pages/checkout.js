import { $ } from "../common/dom.js";
import { AppState, CartState } from "../common/state.js";
import { apiFetchCart, apiCheckout } from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";

/* ===============================
   CHẶN FORM SUBMIT TRUYỀN THỐNG
================================ */
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

  listEl.innerHTML = CartState.cart.items
    .map(
      (item) => `
    <div style="padding:10px 0;border-bottom:1px dashed #eee">
      <span>${item.phone.phoneName} (x${item.quantity})</span>
      <strong>${formatVND(getPhonePrice(item.phone) * item.quantity)}</strong>
    </div>
  `
    )
    .join("");

  const subtotal = CartState.cart.items.reduce(
    (sum, item) => sum + getPhonePrice(item.phone) * item.quantity,
    0
  );

  const subtotalEl = $("#checkout-subtotal");
  const totalEl = $("#checkout-total");
  const deliveryEl = $("#delivery-time");

  if (subtotalEl) subtotalEl.textContent = formatVND(subtotal);
  if (totalEl) totalEl.textContent = formatVND(subtotal + 30000);

  if (deliveryEl) {
    const d = new Date();
    d.setDate(d.getDate() + 3);
    deliveryEl.textContent = d.toLocaleDateString("vi-VN");
  }
}

/* ===============================
   TRANG CHECKOUT (EXPORT CHO main.js)
================================ */
export function initCheckoutPage() {
  blockFormSubmit();
  initAddress2Levels();

  if (AppState.currentUser) {
    const nameEl = $("#checkout-name");
    const phoneEl = $("#checkout-phone");
    if (nameEl) nameEl.value = AppState.currentUser.fullName || "";
    if (phoneEl) phoneEl.value = AppState.currentUser.phone || "";
  }

  apiFetchCart().then(renderSummary);

  const confirmBtn = $("#confirm-order-btn");
  if (!confirmBtn) return;

  confirmBtn.addEventListener("click", async (e) => {
    e.preventDefault();
    e.stopPropagation();

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

    const payload = {
      userId: AppState.currentUser.userId,
      recipientName: name,
      recipientPhone: phone,
      shippingAddress: `${street}, ${wardText}, ${provinceText}`,
      paymentId: paymentMethod,
      couponCode: "",
    };

    console.log("📦 Payload checkout:", payload);

    try {
      confirmBtn.disabled = true;
      confirmBtn.textContent = "ĐANG XỬ LÝ...";

      const result = await apiCheckout(payload);

      if (paymentMethod === "02") {
        window.location.href = `qr-payment.html?amount=${result.totalAmount}&orderId=${result.orderId}`;
      } else {
        window.location.href = `order-success.html?orderId=${result.orderId}`;
      }
    } catch (err) {
      alert("Lỗi đặt hàng: " + err.message);
      confirmBtn.disabled = false;
      confirmBtn.textContent = "XÁC NHẬN ĐẶT HÀNG";
    }
  });
}
