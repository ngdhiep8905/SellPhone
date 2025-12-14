const API = "/api";
let editingId = null;

// [BỔ SUNG] Khai báo các biến DOM element cần thiết
// (Giả định các ID này có trong HTML)
const phoneName = document.getElementById("phoneName");
const price = document.getElementById("price");
const coverImageURL = document.getElementById("coverImageURL");
const brandId = document.getElementById("brandId"); // Selector cho dropdown

// LOAD PRODUCTS VÀ BRANDS KHI TẢI TRANG
window.onload = function () {
    loadBrands();
    loadProducts();
};

function loadProducts() {
    fetch(`${API}/phones`)
    .then(res => res.json())
    .then(data => {
        let html = "";
        data.forEach(p => {
            // [SỬA LỖI 1]: Truy cập Brand Name qua đối tượng lồng (p.brand.brandName)
            const brandNameDisplay = p.brand ? p.brand.brandName : 'N/A';

            html += `
                <tr>
                    <td>${p.phoneId}</td>
                    <td>${p.phoneName}</td>
                    <td>${p.price ? p.price.toLocaleString('vi-VN') : '0'} đ</td>
                    <td><img src="${p.coverImageURL}" width="60" alt="${p.phoneName}"></td>
                    <td>${brandNameDisplay}</td>
                    <td>
                        <button class="btn" onclick="editProduct('${p.phoneId}')">Sửa</button>
                        <button class="btn" style="background:#d63031" onclick="deleteProduct('${p.phoneId}', '${p.phoneName}')">Xóa</button>
                    </td>
                </tr>
            `;
        });
        document.querySelector("#productTable tbody").innerHTML = html;
    })
    .catch(err => console.error("Lỗi tải sản phẩm:", err));
}

function loadBrands() {
    fetch(`${API}/brands`)
    .then(res => res.json())
    .then(data => {
        let br = document.getElementById("brandId");
        if (!br) return;
        br.innerHTML = "";
        data.forEach(b => {
            br.innerHTML += `<option value="${b.brandId}">${b.brandName}</option>`;
        });
    })
    .catch(err => console.error("Lỗi tải Brands:", err));
}

// OPEN MODAL
function openAddModal() {
    editingId = null;
    // Xóa dữ liệu cũ (reset form)
    document.getElementById("phoneName").value = '';
    document.getElementById("price").value = '';
    document.getElementById("coverImageURL").value = '';
    document.getElementById("brandId").value = '';

    document.getElementById("modalTitle").innerText = "Thêm sản phẩm";
    document.getElementById("productModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("productModal").style.display = "none";
}

// SAVE PRODUCT (POST hoặc PUT)
function saveProduct() {
    const brandIdValue = brandId.value;

    // [SỬA LỖI 3]: Cần gửi cấu trúc tương thích với Entity Phones:
    // BrandId được chuyển thành object Brand lồng bên trong (chỉ cần brandId)
    const data = {
        phoneName: phoneName.value,
        price: Number(price.value), // Đảm bảo giá là số
        coverImageURL: coverImageURL.value,
        // Cấu trúc an toàn nhất cho Backend JPA/Hibernate
        brand: {
            brandId: brandIdValue // Chỉ cần khóa ngoại là đủ
        }
    };

    let url = `${API}/phones`;
    let method = "POST";

    if (editingId) {
        url = `${API}/phones/${editingId}`;
        method = "PUT";
    }

    fetch(url, {
        method: method,
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    .then(res => {
        if (!res.ok) throw new Error(`Lỗi Server: HTTP ${res.status}`);
        closeModal();
        loadProducts();
    })
    .catch(err => {
        alert("Lỗi lưu sản phẩm: " + err.message);
        console.error(err);
    });
}

// DELETE PRODUCT
function deleteProduct(id, name) {
    if (!confirm(`Bạn chắc chắn muốn xóa sản phẩm: ${name} (${id})?`)) return;

    fetch(`${API}/phones/${id}`, { method: "DELETE" })
    .then(res => {
         if (!res.ok) throw new Error(`Lỗi Server: HTTP ${res.status}`);
         loadProducts();
    })
    .catch(err => {
        alert("Lỗi xóa sản phẩm: " + err.message);
        console.error(err);
    });
}

// EDIT PRODUCT
function editProduct(id) {
    editingId = id;
    fetch(`${API}/phones/${id}`)
    .then(res => res.json())
    .then(p => {
        phoneName.value = p.phoneName;
        price.value = p.price;
        coverImageURL.value = p.coverImageURL;

        // [SỬA LỖI 4]: Lấy brandId từ đối tượng Brand lồng
        brandId.value = p.brand ? p.brand.brandId : '';

        document.getElementById("modalTitle").innerText = "Sửa sản phẩm";
        document.getElementById("productModal").style.display = "flex";
    })
    .catch(err => console.error("Lỗi tải thông tin sản phẩm:", err));
}

function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}