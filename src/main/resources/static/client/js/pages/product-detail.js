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
    $("#phone-name").textContent = "Thiếu mã sản phẩm trên URL";
    return;
  }

  try {
    const url = `/api/phones/${encodeURIComponent(phoneId)}`;
    console.log("Calling API:", url);

    const res = await fetch(url);
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(`HTTP ${res.status} - ${msg}`);
    }

    const p = await res.json();
    console.log("✅ Dữ liệu sản phẩm:", p);

    $("#phone-name").textContent = p.phoneName;
    $("#phone-price").textContent = formatVND(p.price);
    $("#phone-desc").textContent = p.phoneDescription || "Chưa có mô tả.";

    const imgEl = $("#phone-img");
    if (imgEl) imgEl.src = p.coverImageURL || p.phoneImageThumb || "placeholder.jpg";

    $("#spec-chip").textContent = p.chipset || "N/A";
    $("#spec-ram").textContent = p.ramSize || "N/A";
    $("#spec-storage").textContent = p.storageSize || "N/A";
    $("#spec-screen").textContent = p.screenInfo || "N/A";
    $("#spec-battery").textContent = p.batteryInfo || "N/A";
    $("#spec-color").textContent = p.color || "N/A";
    $("#spec-stock").textContent =
      p.stockQuantity > 0 ? `Còn ${p.stockQuantity} máy` : "Hết hàng";

    const addBtn = $("#btn-add-cart");
    if (addBtn) {
      if (p.stockQuantity <= 0) {
        addBtn.disabled = true;
        addBtn.textContent = "HẾT HÀNG";
      } else {
        addBtn.onclick = async () => {
          await apiAddToCart(phoneId, 1);
          updateCartHeaderCount();
          alert("Đã thêm vào giỏ hàng!");
        };
      }
    }
  } catch (err) {
    console.error("❌ Lỗi API:", err);
    $("#phone-name").textContent = "Lỗi tải thông tin sản phẩm";
  }
}

