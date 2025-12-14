const API = "/api"; // Thay đổi thành /api nếu Backend chạy ở cùng host
let editingId = null;

// Lấy reference các element DOM (Giả định các ID này có tồn tại trong HTML)
const brandName = document.getElementById("brandName");
const modalTitle = document.getElementById("modalTitle");
const brandModal = document.getElementById("brandModal");
const brandTableBody = document.querySelector("#brandTable tbody");

// LOAD BRANDS ON PAGE LOAD
window.onload = function () {
    loadBrands();
};

function loadBrands() {
    fetch(`${API}/brands`)
    .then(res => res.json())
    .then(data => {
        let html = "";
        data.forEach(b => {
            html += `
                <tr>
                    <td>${b.brandId}</td>
                    <td>${b.brandName}</td>
                    <td>
                        <button class="btn" onclick="editBrand('${b.brandId}')">Sửa</button>
                        <button class="btn" style="background:#d63031" onclick="deleteBrand('${b.brandId}')">Xóa</button>
                    </td>
                </tr>
            `;
        });
        brandTableBody.innerHTML = html; // Dùng biến đã khai báo
    })
    .catch(err => console.error("Lỗi tải Brands:", err));
}

// OPEN ADD MODAL
function openAddModal() {
    editingId = null;
    brandName.value = "";
    modalTitle.innerText = "Thêm thương hiệu";
    brandModal.style.display = "flex";
}

// CLOSE MODAL
function closeModal() {
    brandModal.style.display = "none";
}

// SAVE BRAND (POST hoặc PUT)
function saveBrand() {
    const data = {
        brandName: brandName.value
    };

    let url = `${API}/brands`;
    let method = "POST";

    if (editingId) {
        url = `${API}/brands/${editingId}`;
        method = "PUT";
    }

    fetch(url, {
        method: method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
    .then(res => {
        if (!res.ok) throw new Error(`Lỗi Server: HTTP ${res.status}`);
        return res.json();
    })
    .then(() => {
        closeModal();
        loadBrands();
    })
    .catch(err => {
        alert("Lỗi lưu thương hiệu: " + err.message);
        console.error(err);
    });
}

// DELETE BRAND
function deleteBrand(id) {
    if (!confirm("Bạn chắc chắn muốn xóa thương hiệu này?")) return;

    fetch(`${API}/brands/${id}`, { method: "DELETE" })
        .then(res => {
             if (!res.ok) throw new Error(`Lỗi Server: HTTP ${res.status}`);
             loadBrands();
        })
        .catch(err => {
            alert("Lỗi xóa thương hiệu: " + err.message);
            console.error(err);
        });
}

// EDIT BRAND
function editBrand(id) {
    editingId = id;

    fetch(`${API}/brands/${id}`)
        .then(res => {
            if (!res.ok) throw new Error(`Lỗi Server: HTTP ${res.status}`);
            return res.json();
        })
        .then(b => {
            brandName.value = b.brandName;
            modalTitle.innerText = "Sửa thương hiệu";
            brandModal.style.display = "flex";
        })
        .catch(err => {
            alert("Lỗi tải thông tin thương hiệu: " + err.message);
            console.error(err);
        });
}

function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}