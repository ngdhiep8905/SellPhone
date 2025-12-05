const API = "http://localhost:8080/api";

let editingId = null;

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
                        <button class="btn" onclick="editBrand(${b.brandId})">Sửa</button>
                        <button class="btn" style="background:#d63031" onclick="deleteBrand(${b.brandId})">Xóa</button>
                    </td>
                </tr>
            `;
        });
        document.querySelector("#brandTable tbody").innerHTML = html;
    });
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

// SAVE BRAND
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
    .then(() => {
        closeModal();
        loadBrands();
    });
}

// DELETE BRAND
function deleteBrand(id) {
    if (!confirm("Bạn chắc chắn muốn xóa thương hiệu này?")) return;

    fetch(`${API}/brands/${id}`, { method: "DELETE" })
        .then(() => loadBrands());
}

// EDIT BRAND
function editBrand(id) {
    editingId = id;

    fetch(`${API}/brands/${id}`)
        .then(res => res.json())
        .then(b => {
            brandName.value = b.brandName;
            modalTitle.innerText = "Sửa thương hiệu";
            brandModal.style.display = "flex";
        });
}
function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}
