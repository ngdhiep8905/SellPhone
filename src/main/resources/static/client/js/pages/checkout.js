import { $ } from "../common/dom.js";
import { AppState, CartState } from "../common/state.js";
import { apiFetchCart, apiCheckout } from "../common/api.js";
import { formatVND, getPhonePrice } from "../common/helpers.js";

async function initAddress2Levels() {
  const provinceSel = $("#provinces");
  const wardSel = $("#wards");

  try {
    // 1) Lấy danh sách 34 tỉnh/thành (v2)
    const resP = await fetch("https://provinces.open-api.vn/api/v2/");
    const provinces = await resP.json();

    provinceSel.innerHTML =
      '<option value="">Chọn Tỉnh/Thành phố</option>' +
      provinces
        .slice()
        .sort((a, b) => a.name.localeCompare(b.name, "vi"))
        .map((p) => `<option value="${p.code}">${p.name}</option>`)
        .join("");

    // 2) Khi chọn tỉnh -> lấy wards theo tỉnh (v2), KHÔNG dùng depth=3
    provinceSel.onchange = async () => {
      const pCode = provinceSel.value;
      if (!pCode) {
        wardSel.innerHTML =
          '<option value="">Chọn Xã/Phường (Chọn Tỉnh trước)</option>';
        return;
      }

      wardSel.innerHTML = '<option value="">Đang tải danh sách Xã/Phường...</option>';

      try {
        // Cách 1 (ưu tiên): lấy chi tiết 1 tỉnh với depth=2 (nếu endpoint này có trên v2)
        // Nếu v2 không hỗ trợ /p/{code}, catch sẽ fallback sang cách 2.
        const resDetail = await fetch(
          `https://provinces.open-api.vn/api/v2/p/${pCode}?depth=2`
        );
        if (!resDetail.ok) throw new Error("No /p/{code} on v2");
        const detail = await resDetail.json();

        const wards = (detail.wards || [])
          .map((w) => w.name)
          .sort((a, b) => a.localeCompare(b, "vi"));

        wardSel.innerHTML =
          '<option value="">Chọn Xã / Phường / Thị trấn</option>' +
          wards.map((name) => `<option value="${name}">${name}</option>`).join("");
      } catch {
        // Cách 2 (fallback chắc chắn): tải danh sách tỉnh kèm wards với depth=2 rồi lọc theo code
        const resAll = await fetch("https://provinces.open-api.vn/api/v2/?depth=2");
        const all = await resAll.json();

        const p = all.find((x) => String(x.code) === String(pCode));
        const wards = (p?.wards || [])
          .map((w) => w.name)
          .sort((a, b) => a.localeCompare(b, "vi"));

        wardSel.innerHTML =
          '<option value="">Chọn Xã / Phường / Thị trấn</option>' +
          wards.map((name) => `<option value="${name}">${name}</option>`).join("");
      }
    };
  } catch (e) {
    console.error("Lỗi API địa chỉ (v2):", e);
  }
}


export function initCheckoutPage() {
    // Tự điền thông tin user từ AppState
    if (AppState.currentUser) {
        if ($("#checkout-name")) $("#checkout-name").value = AppState.currentUser.fullName || "";
        if ($("#checkout-phone")) $("#checkout-phone").value = AppState.currentUser.phone || "";
    }

    initAddress2Levels();
    apiFetchCart().then(renderSummary);

    const confirmBtn = $("#confirm-order-btn");
    confirmBtn.onclick = async () => {
        const name = $("#checkout-name").value.trim();
        const phone = $("#checkout-phone").value.trim();
        const street = $("#checkout-street").value.trim();
        const provinceText = $("#provinces").options[$("#provinces").selectedIndex]?.text;
        const wardText = $("#wards").value;
        const paymentMethod = $("#checkout-payment-method").value;

        // Validation nghiêm ngặt
        if (!name || !phone || !street || !$("#provinces").value || !wardText) {
            alert("Vui lòng điền đầy đủ các thông tin bắt buộc (*)");
            return;
        }

        const fullAddress = `${street}, ${wardText}, ${provinceText}`;

        try {
            confirmBtn.disabled = true;
            confirmBtn.textContent = "ĐANG XỬ LÝ...";

            const result = await apiCheckout({
                userId: AppState.currentUser.userId,
                recipientName: name,
                recipientPhone: phone,
                shippingAddress: fullAddress,
                paymentId: paymentMethod
            });

            // Chuyển hướng theo phương thức thanh toán
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
    };
}

function renderSummary() {
    const listEl = $("#checkout-items-list");
    if (!CartState.cart?.items) return;

    // Hiển thị danh sách sản phẩm tóm tắt
    listEl.innerHTML = CartState.cart.items.map(item => `
        <div class="sp-cart__row" style="padding: 10px 0; border-bottom: 1px dashed #eee;">
            <span>${item.phone.phoneName} (x${item.quantity})</span>
            <strong>${formatVND(getPhonePrice(item.phone) * item.quantity)}</strong>
        </div>
    `).join('');

    const subtotal = CartState.cart.items.reduce((sum, item) => sum + (getPhonePrice(item.phone) * item.quantity), 0);
    $("#checkout-subtotal").textContent = formatVND(subtotal);
    $("#checkout-total").textContent = formatVND(subtotal + 30000);

    // Hiển thị ngày giao hàng dự kiến (3-5 ngày tới)
    const d = new Date(); d.setDate(d.getDate() + 3);
    if ($("#delivery-time")) $("#delivery-time").textContent = d.toLocaleDateString('vi-VN');
}