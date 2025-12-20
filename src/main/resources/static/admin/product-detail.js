const API = "/api";
let currentId = null;

function getQueryParam(name) {
  const url = new URL(window.location.href);
  return url.searchParams.get(name);
}

window.onload = function () {
  currentId = getQueryParam("id");
  if (!currentId) {
    alert("Thiếu id sản phẩm");
    return;
  }

  loadBrands()
    .then(loadDetail)
    .catch(err => {
      console.error(err);
      alert("Lỗi load dữ liệu: " + err.message);
    });
};

function loadBrands() {
  return fetch(`${API}/brands`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(data => {
      const sel = document.getElementById("d_brandId");
      sel.innerHTML = (data || [])
        .map(b => `<option value="${b.brandId}">${b.brandName}</option>`)
        .join("");
    });
}

function loadDetail() {
  return fetch(`${API}/phones/${encodeURIComponent(currentId)}`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(p => {
      document.getElementById("d_phoneId").value = p.phoneId || "";
      document.getElementById("d_phoneName").value = p.phoneName || "";
      document.getElementById("d_price").value = p.price || 0;
      document.getElementById("d_coverImageURL").value = p.coverImageURL || "";
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
      document.getElementById("d_detailImages").value = images.join("\n");
    });
}

function saveDetail() {
  const detailImagesRaw = document.getElementById("d_detailImages").value || "";
  const detailImages = detailImagesRaw
    .split("\n")
    .map(s => s.trim())
    .filter(Boolean);

  const payload = {
    phoneId: currentId,
    phoneName: document.getElementById("d_phoneName").value.trim(),
    price: Number(document.getElementById("d_price").value),
    coverImageURL: document.getElementById("d_coverImageURL").value.trim(),
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
    detailImages: detailImages
  };

  if (!payload.phoneName) return alert("Tên sản phẩm không được để trống.");
  if (!payload.price || payload.price <= 0) return alert("Giá phải > 0.");

  fetch(`${API}/phones/${encodeURIComponent(currentId)}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  })
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      alert("Đã lưu.");
    })
    .catch(err => {
      console.error(err);
      alert("Lỗi lưu: " + err.message);
    });
}
