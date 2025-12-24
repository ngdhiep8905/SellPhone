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

    const mainImg = $("#pd-main-img");
    const thumbsWrap = $("#pd-thumbs");

    // 1) Lấy danh sách ảnh từ API
    // Bạn cần backend trả ra 1 field dạng mảng, ví dụ: p.imageUrls = ["...","..."]
    // Nếu hiện tại chưa có, mình có fallback: lấy từ chuỗi p.images hoặc p.phoneImages rồi split(",")
    const images =
      (Array.isArray(p.imageUrls) && p.imageUrls.length ? p.imageUrls : null) ||
      (typeof p.images === "string" && p.images.trim() ? p.images.split(",").map(s => s.trim()) : null) ||
      (typeof p.phoneImages === "string" && p.phoneImages.trim() ? p.phoneImages.split(",").map(s => s.trim()) : null) ||
      // fallback cuối: 1 ảnh cover
      [p.coverImageURL || p.phoneImageThumb || "/img/noimage.png"];

    let activeIndex = 0;

    // 2) Set ảnh chính
    function setMain(i) {
      activeIndex = i;
      if (mainImg) mainImg.src = images[i] || "/img/noimage.png";

      // update active thumb
      if (thumbsWrap) {
        [...thumbsWrap.querySelectorAll(".sp-thumb")].forEach((el, idx) => {
          el.classList.toggle("is-active", idx === i);
        });
      }
    }

    // 3) Render thumbs
    if (thumbsWrap) {
      thumbsWrap.innerHTML = "";
      images.forEach((src, idx) => {
        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "sp-thumb" + (idx === 0 ? " is-active" : "");
        btn.innerHTML = `<img src="${src}" alt="Ảnh ${idx + 1}">`;
        btn.addEventListener("click", () => setMain(idx));
        thumbsWrap.appendChild(btn);
      });
    }

    // 4) Default ảnh đầu
    setMain(0);


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

