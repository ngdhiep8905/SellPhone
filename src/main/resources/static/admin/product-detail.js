const API = "/api";
let currentId = null;

function getQueryParam(name) {
  const url = new URL(window.location.href);
  return url.searchParams.get(name);
}

async function uploadCoverImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API}/files/upload`, { method: "POST", body: formData });
  if (!res.ok) throw new Error(`Upload failed: HTTP ${res.status}`);

  const data = await res.json(); // { url: "/images/xxx.jpg" }
  return data.url;
}

window.onload = async function () {
  currentId = getQueryParam("id");
  if (!currentId) return alert("Thiếu id sản phẩm");

  try {
    await loadBrands();
    await loadDetail();

    // setup preview cho file
    const fileInput = document.getElementById("d_coverImageFile");
    const preview = document.getElementById("d_coverPreview");

    if (fileInput && preview) {
      fileInput.addEventListener("change", () => {
        const f = fileInput.files?.[0];
        if (!f) return;
        preview.src = URL.createObjectURL(f);
        preview.style.display = "block";
      });
    }
  } catch (e) {
    console.error(e);
    alert("Lỗi load dữ liệu: " + e.message);
  }
};

async function loadBrands() {
  const res = await fetch(`${API}/brands`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const data = await res.json();

  const sel = document.getElementById("d_brandId");
  sel.innerHTML = (data || [])
    .map(b => `<option value="${b.brandId}">${b.brandName}</option>`)
    .join("");
}

async function loadDetail() {
  const res = await fetch(`${API}/phones/${encodeURIComponent(currentId)}`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const p = await res.json();

  document.getElementById("d_phoneId").value = p.phoneId || "";
  document.getElementById("d_phoneName").value = p.phoneName || "";
  document.getElementById("d_price").value = p.price || 0;
  document.getElementById("d_brandId").value = p.brandId || "";
  document.getElementById("d_stockQuantity").value = p.stockQuantity ?? 0;
  document.getElementById("d_status").value = p.status || "ACTIVE";

  document.getElementById("d_chipset").value = p.chipset || "";
  document.getElementById("d_ramSize").value = p.ramSize || "";
  document.getElementById("d_storageSize").value = p.storageSize || "";
  document.getElementById("d_screenInfo").value = p.screenInfo || "";
  document.getElementById("d_batteryInfo").value = p.batteryInfo || "";
  document.getElementById("d_rearCamera").value = p.rearCamera || "";
  document.getElementById("d_frontCamera").value = p.frontCamera || "";
  document.getElementById("d_osVersion").value = p.osVersion || "";
  document.getElementById("d_color").value = p.color || "";
  document.getElementById("d_phoneDescription").value = p.phoneDescription || "";

  const images = Array.isArray(p.detailImages) ? p.detailImages : [];
  const detailBox = document.getElementById("d_detailImages");
  if (detailBox) detailBox.value = images.join("\n");

  // Preview ảnh đại diện hiện có (từ DB)
  const preview = document.getElementById("d_coverPreview");
  if (preview) {
    if (p.coverImageURL) {
      preview.src = p.coverImageURL;
      preview.style.display = "block";
    } else {
      preview.style.display = "none";
      preview.src = "";
    }
  }
}

async function saveDetail() {
  try {
    // nếu có chọn file mới thì upload và lấy url mới
    const fileInput = document.getElementById("d_coverImageFile");
    let coverImageURL = null;

    const chosenFile = fileInput?.files?.[0];
    if (chosenFile) {
      coverImageURL = await uploadCoverImage(chosenFile);
    } else {
      // không chọn mới => giữ nguyên cover hiện có bằng cách lấy từ preview src
      const preview = document.getElementById("d_coverPreview");
      coverImageURL = (preview?.src || "").trim() || null;
    }

    const detailImagesRaw = document.getElementById("d_detailImages")?.value || "";
    const detailImages = detailImagesRaw
      .split("\n")
      .map(s => s.trim())
      .filter(Boolean);

    const payload = {
      phoneId: currentId,
      phoneName: document.getElementById("d_phoneName").value.trim(),
      price: Number(document.getElementById("d_price").value),
      brandId: document.getElementById("d_brandId").value,
      stockQuantity: Number(document.getElementById("d_stockQuantity").value),
      status: document.getElementById("d_status").value,

      chipset: document.getElementById("d_chipset").value.trim(),
      ramSize: document.getElementById("d_ramSize").value.trim(),
      storageSize: document.getElementById("d_storageSize").value.trim(),
      screenInfo: document.getElementById("d_screenInfo").value.trim(),
      batteryInfo: document.getElementById("d_batteryInfo").value.trim(),
      rearCamera: document.getElementById("d_rearCamera").value.trim(),
      frontCamera: document.getElementById("d_frontCamera").value.trim(),
      osVersion: document.getElementById("d_osVersion").value.trim(),
      color: document.getElementById("d_color").value.trim(),

      phoneDescription: document.getElementById("d_phoneDescription").value,
      coverImageURL: coverImageURL,
      detailImages: detailImages
    };

    if (!payload.phoneName) return alert("Tên sản phẩm không được để trống.");
    if (!payload.price || payload.price <= 0) return alert("Giá phải > 0.");

    const res = await fetch(`${API}/phones/${encodeURIComponent(currentId)}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    alert("Đã lưu.");
    await loadDetail(); // reload để preview cập nhật theo DB
  } catch (e) {
    console.error(e);
    alert("Lỗi lưu: " + e.message);
  }
}
