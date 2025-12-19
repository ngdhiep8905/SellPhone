import { $ } from "../common/dom.js";
import { formatVND } from "../common/helpers.js";
import { apiAddToCart } from "../common/api.js";
import { AppState } from "../common/state.js";
import { updateCartHeaderCount } from "../common/header.js";

export async function initProductDetailPage() {
    console.log("🚀 Đang khởi tạo trang chi tiết...");

    const params = new URLSearchParams(window.location.search);
    const phoneId = params.get("id");

    if (!phoneId) {
        console.error("❌ Không lấy được ID từ URL");
        return;
    }

    try {
        // Gọi đúng địa chỉ Backend của bro
        const res = await fetch(`http://localhost:8082/api/phones/${phoneId}`);
        if (!res.ok) throw new Error("Sản phẩm không tồn tại trong Database");

        const p = await res.json();
        console.log("✅ Dữ liệu sản phẩm:", p);

        // Đổ dữ liệu vào HTML (Sử dụng đúng các ID trong file HTML bro gửi)
        $("#phone-name").textContent = p.phoneName;
        $("#phone-price").textContent = formatVND(p.price);
        $("#phone-desc").textContent = p.phoneDescription || "Chưa có mô tả.";

        // Đổ ảnh (Kiểm tra lại tên trường coverImageURL hoặc phoneImageThumb)
        const imgEl = $("#phone-img");
        if (imgEl) imgEl.src = p.coverImageURL || p.phoneImageThumb || 'placeholder.jpg';

        // Đổ thông số kỹ thuật
        $("#spec-chip").textContent = p.chipset || "N/A";
        $("#spec-ram").textContent = p.ramSize || "N/A";
        $("#spec-storage").textContent = p.storageSize || "N/A";
        $("#spec-screen").textContent = p.screenInfo || "N/A";
        $("#spec-battery").textContent = p.batteryInfo || "N/A";
        $("#spec-color").textContent = p.color || "N/A";
        $("#spec-stock").textContent = p.stockQuantity > 0 ? `Còn ${p.stockQuantity} máy` : "Hết hàng";

        // Xử lý nút giỏ hàng
        const addBtn = $("#btn-add-cart");
        if (addBtn) {
            if (p.stockQuantity <= 0) {
                addBtn.disabled = true;
                addBtn.textContent = "HẾT HÀNG";
            } else {
                addBtn.onclick = async () => {
                    await apiAddToCart(phoneId, 1);
                    updateCartHeaderCount();
                    alert("Đã thêm vào giỏ hàng! 🛒");
                };
            }
        }

    } catch (err) {
        console.error("❌ Lỗi API:", err);
        $("#phone-name").textContent = "Lỗi tải thông tin sản phẩm";
    }
}
