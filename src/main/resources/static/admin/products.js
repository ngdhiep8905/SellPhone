const API = "/api";
let cachedProducts = [];
let dragIndex = null;

/* ================== FORM ELEMENTS ================== */
const phoneName = document.getElementById("phoneName");
const price = document.getElementById("price");
const brandId = document.getElementById("brandId");
const stockQuantity = document.getElementById("stockQuantity");
const status = document.getElementById("status");
const chipset = document.getElementById("chipset");
const ramSize = document.getElementById("ramSize");
const storageSize = document.getElementById("storageSize");
const screenInfo = document.getElementById("screenInfo");
const batteryInfo = document.getElementById("batteryInfo");
const rearCamera = document.getElementById("rearCamera");
const frontCamera = document.getElementById("frontCamera");
const osVersion = document.getElementById("osVersion");
const color = document.getElementById("color");
const phoneDescription = document.getElementById("phoneDescription");

const coverImageFile = document.getElementById("coverImageFile");
const coverPreview = document.getElementById("coverPreview");

const detailImagesFile = document.getElementById("detailImagesFile");
const detailPreview = document.getElementById("detailPreview");
const dropZone = document.getElementById("dropZone");

/* ================== DETAIL IMAGES ================== */
const MAX_DETAIL_IMAGES = 6;
let selectedDetailFiles = [];

/* ================== COVER PREVIEW ================== */
if (coverImageFile && coverPreview) {
  coverImageFile.addEventListener("change", () => {
    const f = coverImageFile.files[0];
    if (!f) return;
    coverPreview.src = URL.createObjectURL(f);
    coverPreview.style.display = "block";
  });
}

/* ================== DETAIL IMAGE HANDLING ================== */
dropZone.addEventListener("click", () => detailImagesFile.click());

dropZone.addEventListener("dragover", e => {
  e.preventDefault();
  dropZone.classList.add("dragover");
});
dropZone.addEventListener("dragleave", () => dropZone.classList.remove("dragover"));
dropZone.addEventListener("drop", e => {
  e.preventDefault();
  dropZone.classList.remove("dragover");
  handleDetailFiles(e.dataTransfer.files);
});

detailImagesFile.addEventListener("change", e => {
  handleDetailFiles(e.target.files);
});

function handleDetailFiles(files) {
  for (const f of files) {
    if (selectedDetailFiles.length >= MAX_DETAIL_IMAGES) {
      alert("Tối đa 6 ảnh chi tiết");
      break;
    }
    selectedDetailFiles.push(f);
  }
  renderDetailPreview();
}

function renderDetailPreview() {
  detailPreview.innerHTML = "";

  selectedDetailFiles.forEach((file, index) => {
    const item = document.createElement("div");
    item.className = "detail-item";
    item.draggable = true;
    item.dataset.index = index;

    item.innerHTML = `
      <span class="remove">×</span>
      <img src="${URL.createObjectURL(file)}">
      <div class="name">${file.name}</div>
      <div class="size">${(file.size / 1024).toFixed(1)} KB</div>
    `;

    /* ❌ xoá */
    item.querySelector(".remove").onclick = () => {
      selectedDetailFiles.splice(index, 1);
      renderDetailPreview();
    };

    /* ===== DRAG START ===== */
    item.addEventListener("dragstart", e => {
      dragIndex = index;
      item.style.opacity = "0.4";
    });

    /* ===== DRAG END ===== */
    item.addEventListener("dragend", () => {
      dragIndex = null;
      item.style.opacity = "1";
    });

    /* ===== DRAG OVER ===== */
    item.addEventListener("dragover", e => {
      e.preventDefault();
      item.style.border = "2px dashed #0ea5e9";
    });

    /* ===== DRAG LEAVE ===== */
    item.addEventListener("dragleave", () => {
      item.style.border = "1px solid rgba(0,0,0,0.1)";
    });

    /* ===== DROP ===== */
    item.addEventListener("drop", e => {
      e.preventDefault();
      item.style.border = "1px solid rgba(0,0,0,0.1)";

      const targetIndex = Number(item.dataset.index);
      if (dragIndex === null || dragIndex === targetIndex) return;

      const moved = selectedDetailFiles.splice(dragIndex, 1)[0];
      selectedDetailFiles.splice(targetIndex, 0, moved);

      renderDetailPreview();
    });

    detailPreview.appendChild(item);
  });
}


function removeDetailImage(index) {
  selectedDetailFiles.splice(index, 1);
  renderDetailPreview();
}

/* ================== UPLOAD ================== */
async function uploadCoverImage(file) {
  const fd = new FormData();
  fd.append("file", file);
  const res = await fetch("/api/files/upload", { method: "POST", body: fd });
  const data = await res.json();
  return data.url;
}

async function uploadManyImages(files) {
  const urls = [];
  for (const f of files) {
    urls.push(await uploadCoverImage(f));
  }
  return urls;
}

/* ================== LOAD DATA ================== */
window.onload = () => {
  loadBrands();
  loadProducts();
  setupBackdropClose();
};

function loadProducts() {
  fetch(`${API}/phones`)
    .then(r => r.json())
    .then(d => {
      cachedProducts = d || [];
      renderTable(cachedProducts);
    });
}

function renderTable(data) {
  const tbody = document.querySelector("#productTable tbody");
  tbody.innerHTML = data.map(p => `
    <tr onclick="viewProduct('${p.phoneId}')">
      <td>${p.phoneId}</td>
      <td>${p.phoneName}</td>
      <td>${Number(p.price).toLocaleString()} đ</td>
      <td>${p.stockQuantity ?? 0}</td>
      <td><img src="${p.coverImageURL || "/img/noimage.png"}" width="60"></td>
      <td>${p.brandName || "N/A"}</td>
      <td onclick="event.stopPropagation()">
        <button class="btn" onclick="viewProduct('${p.phoneId}')">Xem</button>
        <button class="btn btn-danger" onclick="deleteProduct('${p.phoneId}','${p.phoneName}')">Xóa</button>
      </td>
    </tr>
  `).join("");
}

function viewProduct(id) {
  window.open(`product-detail.html?id=${id}`, "_blank");
}

function loadBrands() {
  fetch(`${API}/brands`)
    .then(r => r.json())
    .then(d => {
      brandId.innerHTML = d.map(b =>
        `<option value="${b.brandId}">${b.brandName}</option>`
      ).join("");
    });
}

/* ================== SAVE PRODUCT ================== */
async function saveProduct() {
  const coverUrl = coverImageFile.files[0]
    ? await uploadCoverImage(coverImageFile.files[0])
    : "";

  const detailUrls = await uploadManyImages(selectedDetailFiles);

  const data = {
    phoneName: phoneName.value.trim(),
    price: Number(price.value),
    brandId: brandId.value,
    stockQuantity: Number(stockQuantity.value),
    status: status.value,
    chipset: chipset.value,
    ramSize: ramSize.value,
    storageSize: storageSize.value,
    screenInfo: screenInfo.value,
    batteryInfo: batteryInfo.value,
    rearCamera: rearCamera.value,
    frontCamera: frontCamera.value,
    osVersion: osVersion.value,
    color: color.value,
    phoneDescription: phoneDescription.value,
    coverImageURL: coverUrl,
    detailImages: detailUrls
  };

  await fetch(`${API}/phones`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });

  closeModal();
  loadProducts();
  alert("Đã thêm sản phẩm");
}

/* ================== MODAL ================== */
function openAddModal() {
  selectedDetailFiles = [];
  renderDetailPreview();
  document.getElementById("productModal").style.display = "flex";
}
function closeModal() {
  document.getElementById("productModal").style.display = "none";
}
function setupBackdropClose() {
  const m = document.getElementById("productModal");
  m.addEventListener("click", e => {
    if (e.target === m) closeModal();
  });
}

function deleteProduct(id, name) {
  if (!confirm(`Xóa ${name}?`)) return;
  fetch(`${API}/phones/${id}`, { method: "DELETE" })
    .then(() => loadProducts());
}
