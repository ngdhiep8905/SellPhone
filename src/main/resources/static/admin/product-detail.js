const API = "/api";
let currentId = null;

function getQueryParam(name) {
  const url = new URL(window.location.href);
  return url.searchParams.get(name);
}

async function uploadOneImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API}/files/upload`, { method: "POST", body: formData });
  if (!res.ok) throw new Error(`Upload failed: HTTP ${res.status}`);

  const data = await res.json(); // { url: "/images/xxx.jpg" }
  return data.url;
}

async function uploadManyImages(files) {
  const arr = Array.from(files || []);
  if (!arr.length) return [];

  const urls = [];
  for (const f of arr) {
    const url = await uploadOneImage(f);
    urls.push(url);
  }
  return urls;
}

function renderDetailPreview(urls) {
  const box = document.getElementById("d_detailPreview");
  if (!box) return;

  box.innerHTML = "";
  (urls || []).forEach((url) => {
    const img = document.createElement("img");
    img.src = url;
    img.style.width = "90px";
    img.style.height = "90px";
    img.style.objectFit = "cover";
    img.style.borderRadius = "12px";
    img.style.border = "1px solid rgba(0,0,0,0.08)";
    box.appendChild(img);
  });
}

window.onload = async function () {
  currentId = getQueryParam("id");
  if (!currentId) return alert("Thiếu id sản phẩm");

  try {
    await loadBrands();
    await loadDetail();

    // preview cover khi chọn file mới
    const coverFileInput = document.getElementById("d_coverImageFile");
    const coverPreview = document.getElementById("d_coverPreview");

    if (coverFileInput && coverPreview) {
      coverFileInput.addEventListener("change", () => {
        const f = coverFileInput.files?.[0];
        if (!f) return;
        coverPreview.src = URL.createObjectURL(f);
        coverPreview.style.display = "block";
      });
    }

    // upload nhiều ảnh chi tiết -> auto thêm vào textarea + preview
    const detailFileInput = document.getElementById("d_detailImagesFile");
    if (detailFileInput) {
      detailFileInput.addEventListener("change", async () => {
        try {
          const urls = await uploadManyImages(detailFileInput.files);

          // append vào textarea (mỗi dòng 1 url)
          const ta = document.getElementById("d_detailImages");
          const current = (ta?.value || "")
            .split("\n")
            .map((s) => s.trim())
            .filter(Boolean);

          const merged = [...current, ...urls];
          if (ta) ta.value = merged.join("\n");

          // preview theo merged list
          renderDetailPreview(merged);

          // reset input để lần sau chọn lại vẫn trigger change
          detailFileInput.value = "";
        } catch (e) {
          console.error(e);
          alert("Lỗi upload ảnh chi tiết: " + e.message);
        }
      });
    }

    // khi admin sửa textarea thủ công -> cập nhật preview
    const ta = document.getElementById("d_detailImages");
    if (ta) {
      ta.addEventListener("input", () => {
        const urls = (ta.value || "")
          .split("\n")
          .map((s) => s.trim())
          .filter(Boolean);
        renderDetailPreview(urls);
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
    .map((b) => `<option value="${b.brandId}">${b.brandName}</option>`)
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

  renderDetailPreview(images);

  // cover preview từ DB
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
    const coverFileInput = document.getElementById("d_coverImageFile");
    const coverPreview = document.getElementById("d_coverPreview");
    let coverImageURL = null;

    // nếu chọn file mới -> upload
    const chosenFile = coverFileInput?.files?.[0];
    if (chosenFile) {
      coverImageURL = await uploadOneImage(chosenFile);
    } else {
      // không chọn mới => giữ theo preview hiện tại (DB)
      coverImageURL = (coverPreview?.getAttribute("src") || "").trim() || null;
    }

    const detailImagesRaw = document.getElementById("d_detailImages")?.value || "";
    const detailImages = detailImagesRaw
      .split("\n")
      .map((s) => s.trim())
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

    if (!res.ok) {
      const msg = await res.text().catch(() => "");
      throw new Error(msg || `HTTP ${res.status}`);
    }

    alert("Đã lưu.");
    await loadDetail();
  } catch (e) {
    console.error(e);
    alert("Lỗi lưu: " + e.message);
  }
}
