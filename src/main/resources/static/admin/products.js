
const API = "/api";
let cachedProducts = [];

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


if (coverImageFile && coverPreview) {
  coverImageFile.addEventListener("change", () => {
    const f = coverImageFile.files?.[0];
    if (!f) {
      coverPreview.style.display = "none";
      coverPreview.src = "";
      return;
    }
    coverPreview.src = URL.createObjectURL(f);
    coverPreview.style.display = "block";
  });
}

async function uploadCoverImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`/api/files/upload`, { method: "POST", body: formData });
  if (!res.ok) throw new Error(`Upload failed: HTTP ${res.status}`);

  const data = await res.json();
  return data.url; // "/images/xxx.jpg"
}



window.onload = function () {
  loadBrands();
  loadProducts();
};

function loadProducts() {
  fetch(`${API}/phones`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(data => {
      cachedProducts = data || [];
      renderTable(cachedProducts);
    })
    .catch(err => console.error("Lỗi tải sản phẩm:", err));
}

function renderTable(data) {
  let html = "";

  data.forEach(p => {
    const img = p.coverImageURL || "/img/noimage.png";
    const brandNameDisplay = p.brandName || "N/A";
    const stock = (p.stockQuantity ?? 0);

    html += `
      <tr onclick="viewProduct('${p.phoneId}')" style="cursor:pointer;">
        <td>${p.phoneId}</td>
        <td>${p.phoneName}</td>
        <td>${p.price ? Number(p.price).toLocaleString("vi-VN") : "0"} đ</td>
        <td>${stock}</td>
        <td><img src="${img}" width="60" alt="${p.phoneName}"></td>
        <td>${brandNameDisplay}</td>
        <td onclick="event.stopPropagation();">
          <button class="btn" onclick="viewProduct('${p.phoneId}')">Xem</button>
          <button class="btn" style="background:#d63031" onclick="deleteProduct('${p.phoneId}', '${escapeQuotes(p.phoneName)}')">Xóa</button>
        </td>
      </tr>
    `;
  });

  document.querySelector("#productTable tbody").innerHTML = html;
}

function escapeQuotes(s) {
  return (s || "").replace(/'/g, "\\'");
}

function viewProduct(id) {
  // mở tab/cửa sổ mới để sửa chi tiết
  window.open(`product-detail.html?id=${encodeURIComponent(id)}`, "_blank");
}

function loadBrands() {
  fetch(`${API}/brands`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(data => {
      brandId.innerHTML = "";
      (data || []).forEach(b => {
        brandId.innerHTML += `<option value="${b.brandId}">${b.brandName}</option>`;
      });
    })
    .catch(err => console.error("Lỗi tải Brands:", err));
}

// MODAL THÊM
function openAddModal() {
  phoneName.value = "";
  price.value = "";
  brandId.value = "";

  stockQuantity.value = 0;
  status.value = "ACTIVE";

  chipset.value = "";
  ramSize.value = "";
  storageSize.value = "";
  screenInfo.value = "";
  batteryInfo.value = "";
  rearCamera.value = "";
  frontCamera.value = "";
  osVersion.value = "";
  color.value = "";
  phoneDescription.value = "";

  // reset file + preview
  if (coverImageFile) coverImageFile.value = "";
  if (coverPreview) {
    coverPreview.style.display = "none";
    coverPreview.src = "";
  }

  document.getElementById("modalTitle").innerText = "Thêm sản phẩm";
  document.getElementById("productModal").style.display = "flex";
}



function closeModal() {
  document.getElementById("productModal").style.display = "none";
}

// POST tạo sản phẩm (sửa thông tin sẽ làm ở product-detail)
async function saveProduct() {
  try {
    const file = coverImageFile.files?.[0];
    let coverUrl = ""; // đường dẫn lưu DB

    if (file) {
      coverUrl = await uploadCoverImage(file);
    }

    const data = {
      phoneName: phoneName.value.trim(),
      price: Number(price.value),
      coverImageURL: coverUrl,     // DB vẫn lưu string path/url
      brandId: brandId.value,

      stockQuantity: Number(stockQuantity.value),
      status: status.value,

      chipset: chipset.value.trim(),
      ramSize: ramSize.value.trim(),
      storageSize: storageSize.value.trim(),
      screenInfo: screenInfo.value.trim(),
      batteryInfo: batteryInfo.value.trim(),
      rearCamera: rearCamera.value.trim(),
      frontCamera: frontCamera.value.trim(),
      osVersion: osVersion.value.trim(),
      color: color.value.trim(),
      phoneDescription: phoneDescription.value.trim()
    };

    if (!data.phoneName) return alert("Tên sản phẩm không được để trống.");
    if (!data.price || data.price <= 0) return alert("Giá phải > 0.");
    if (!data.brandId) return alert("Vui lòng chọn thương hiệu.");

    const res = await fetch(`${API}/phones`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    closeModal();
    loadProducts();
    alert("Đã thêm sản phẩm.");
  } catch (err) {
    console.error(err);
    alert("Lỗi thêm sản phẩm: " + err.message);
  }
}



function deleteProduct(id, name) {
  if (!confirm(`Bạn chắc chắn muốn xóa sản phẩm: ${name} (${id})?`)) return;

  fetch(`${API}/phones/${encodeURIComponent(id)}`, { method: "DELETE" })
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      loadProducts();
    })
    .catch(err => {
      console.error(err);
      alert("Lỗi xóa sản phẩm: " + err.message);
    });
}

// MODAL TỒN KHO (áp dụng toàn bộ)
function openStockModal() {
  const stockList = document.getElementById("stockList");

  stockList.innerHTML = cachedProducts.map(p => `
    <div style="display:flex; align-items:center; justify-content:space-between; gap:12px; padding:8px 0; border-bottom:1px solid #eee;">
      <div style="flex:1;">
        <div style="font-weight:600;">${p.phoneName}</div>
        <div style="font-size:12px; opacity:0.8;">${p.phoneId} • ${p.brandName || "N/A"}</div>
      </div>
      <input type="number" min="0" style="width:120px;"
        data-phone-id="${p.phoneId}"
        value="${p.stockQuantity ?? 0}"
      />
    </div>
  `).join("");

  document.getElementById("stockModal").style.display = "flex";
}

function closeStockModal() {
  document.getElementById("stockModal").style.display = "none";
}

function saveStockChanges() {
  const inputs = document.querySelectorAll("#stockList input[data-phone-id]");
  const updates = [];

  inputs.forEach(inp => {
    const id = inp.getAttribute("data-phone-id");
    const newQty = Number(inp.value);

    const p = cachedProducts.find(x => x.phoneId === id);
    if (!p) return;

    const oldQty = (p.stockQuantity ?? 0);
    if (oldQty !== newQty) {
      // gửi DTO phẳng lên PUT
      updates.push({ ...p, stockQuantity: newQty });
    }
  });

  if (updates.length === 0) {
    closeStockModal();
    return;
  }

  Promise.all(
    updates.map(p =>
      fetch(`${API}/phones/${encodeURIComponent(p.phoneId)}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(p)
      }).then(res => {
        if (!res.ok) throw new Error(`Update ${p.phoneId} failed: HTTP ${res.status}`);
      })
    )
  )
    .then(() => {
      closeStockModal();
      loadProducts();
      alert("Đã cập nhật tồn kho.");
    })
    .catch(err => {
      console.error(err);
      alert("Lỗi cập nhật tồn kho: " + err.message);
    });
}

function logout() {
  localStorage.removeItem("isAdmin");
  window.location.href = "admin-login.html";
}
