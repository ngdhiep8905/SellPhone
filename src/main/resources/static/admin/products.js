const API = "http://localhost:8080/api";

let editingId = null;

// LOAD PRODUCTS
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
            html += `
                <tr>
                    <td>${p.phoneId}</td>
                    <td>${p.phoneName}</td>
                    <td>${p.price.toLocaleString()} đ</td>
                    <td><img src="${p.coverImageURL}" width="60"></td>
                    <td>${p.brandName}</td>
                    <td>
                        <button class="btn" onclick="editProduct(${p.phoneId})">Sửa</button>
                        <button class="btn" style="background:#d63031" onclick="deleteProduct(${p.phoneId})">Xóa</button>
                    </td>
                </tr>
            `;
        });
        document.querySelector("#productTable tbody").innerHTML = html;
    });
}

function loadBrands() {
    fetch(`${API}/brands`)
    .then(res => res.json())
    .then(data => {
        let br = document.getElementById("brandId");
        br.innerHTML = "";
        data.forEach(b => {
            br.innerHTML += `<option value="${b.brandId}">${b.brandName}</option>`;
        });
    });
}

// OPEN MODAL
function openAddModal() {
    editingId = null;
    document.getElementById("modalTitle").innerText = "Thêm sản phẩm";
    document.getElementById("productModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("productModal").style.display = "none";
}

// SAVE PRODUCT
function saveProduct() {
    const data = {
        phoneName: phoneName.value,
        price: price.value,
        coverImageURL: coverImageURL.value,
        brandId: brandId.value
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
    .then(() => {
        closeModal();
        loadProducts();
    });
}

// DELETE PRODUCT
function deleteProduct(id) {
    if (!confirm("Bạn chắc chắn muốn xóa sản phẩm này?")) return;

    fetch(`${API}/phones/${id}`, { method: "DELETE" })
    .then(() => loadProducts());
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
        brandId.value = p.brandId;

        document.getElementById("modalTitle").innerText = "Sửa sản phẩm";
        document.getElementById("productModal").style.display = "flex";
    });
}
function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}
