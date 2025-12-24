const API = "/api";
const MAX_DETAIL_IMAGES = 6;

let currentId = null;
let detailImages = []; // { file?, url, name, size }

function getQueryParam(name) {
  return new URL(window.location.href).searchParams.get(name);
}

/* ================= UPLOAD ================= */

async function uploadOneImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API}/files/upload`, { method: "POST", body: formData });
  if (!res.ok) throw new Error(`Upload failed ${res.status}`);
  const data = await res.json();
  return data.url;
}

/* ================= PREVIEW ================= */

function renderDetailPreview() {
  const box = document.getElementById("d_detailPreview");
  box.innerHTML = "";

  detailImages.forEach((img, index) => {
    const div = document.createElement("div");
    div.className = "detail-item";
    div.draggable = true;
    div.dataset.index = index;

    div.innerHTML = `
      <span class="remove" onclick="removeDetailImage(${index})">×</span>
      <img src="${img.url}">
      <div style="margin-top:6px;">
        <div>${img.name}</div>
        <div style="opacity:.7">${(img.size / 1024).toFixed(1)} KB</div>
      </div>
    `;

    // drag events
    div.addEventListener("dragstart", e => {
      e.dataTransfer.setData("from", index);
    });
    div.addEventListener("dragover", e => e.preventDefault());
    div.addEventListener("drop", e => {
      const from = +e.dataTransfer.getData("from");
      const to = index;
      if (from !== to) {
        detailImages.splice(to, 0, detailImages.splice(from, 1)[0]);
        renderDetailPreview();
      }
    });

    box.appendChild(div);
  });
}

function removeDetailImage(i) {
  detailImages.splice(i, 1);
  renderDetailPreview();
}

/* ================= LOAD ================= */

window.onload = async () => {
  currentId = getQueryParam("id");
  if (!currentId) return alert("Thiếu id");

  await loadBrands();
  await loadDetail();
  setupDetailUpload();
};

async function loadBrands() {
  const res = await fetch(`${API}/brands`);
  const data = await res.json();
  document.getElementById("d_brandId").innerHTML =
    data.map(b => `<option value="${b.brandId}">${b.brandName}</option>`).join("");
}

async function loadDetail() {
  const res = await fetch(`${API}/phones/${currentId}`);
  const p = await res.json();

  d_phoneId.value = p.phoneId;
  d_phoneName.value = p.phoneName;
  d_price.value = p.price;
  d_brandId.value = p.brandId;
  d_stockQuantity.value = p.stockQuantity ?? 0;
  d_status.value = p.status;

  d_chipset.value = p.chipset || "";
  d_ramSize.value = p.ramSize || "";
  d_storageSize.value = p.storageSize || "";
  d_screenInfo.value = p.screenInfo || "";
  d_batteryInfo.value = p.batteryInfo || "";
  d_rearCamera.value = p.rearCamera || "";
  d_frontCamera.value = p.frontCamera || "";
  d_osVersion.value = p.osVersion || "";
  d_color.value = p.color || "";
  d_phoneDescription.value = p.phoneDescription || "";

  // cover
  if (p.coverImageURL) {
    d_coverPreview.src = p.coverImageURL;
    d_coverPreview.style.display = "block";
  }

  // detail images from DB
  detailImages = (p.detailImages || []).map(url => ({
    url,
    name: url.split("/").pop(),
    size: 0
  }));

  renderDetailPreview();
}

/* ================= DROPZONE ================= */

function setupDetailUpload() {
  const dz = document.getElementById("d_dropZone");
  const input = document.getElementById("d_detailImagesFile");

  dz.onclick = () => input.click();

  dz.ondragover = e => {
    e.preventDefault();
    dz.classList.add("dragover");
  };

  dz.ondragleave = () => dz.classList.remove("dragover");

  dz.ondrop = e => {
    e.preventDefault();
    dz.classList.remove("dragover");
    handleFiles(e.dataTransfer.files);
  };

  input.onchange = () => handleFiles(input.files);
}

function handleFiles(files) {
  for (const f of files) {
    if (detailImages.length >= MAX_DETAIL_IMAGES) {
      alert("Tối đa 6 ảnh");
      break;
    }
    detailImages.push({
      file: f,
      url: URL.createObjectURL(f),
      name: f.name,
      size: f.size
    });
  }
  renderDetailPreview();
}

/* ================= SAVE ================= */

async function saveDetail() {
  try {
    // upload ảnh mới
    for (const img of detailImages) {
      if (img.file) {
        img.url = await uploadOneImage(img.file);
        delete img.file;
      }
    }

    const payload = {
      phoneId: currentId,
      phoneName: d_phoneName.value.trim(),
      price: Number(d_price.value),
      brandId: d_brandId.value,
      stockQuantity: Number(d_stockQuantity.value),
      status: d_status.value,

      chipset: d_chipset.value.trim(),
      ramSize: d_ramSize.value.trim(),
      storageSize: d_storageSize.value.trim(),
      screenInfo: d_screenInfo.value.trim(),
      batteryInfo: d_batteryInfo.value.trim(),
      rearCamera: d_rearCamera.value.trim(),
      frontCamera: d_frontCamera.value.trim(),
      osVersion: d_osVersion.value.trim(),
      color: d_color.value.trim(),
      phoneDescription: d_phoneDescription.value,

      coverImageURL: d_coverPreview.src || null,
      detailImages: detailImages.map(i => i.url)
    };

    const res = await fetch(`${API}/phones/${currentId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error("Lưu thất bại");
    alert("Đã lưu");
    await loadDetail();
  } catch (e) {
    alert(e.message);
  }
}
