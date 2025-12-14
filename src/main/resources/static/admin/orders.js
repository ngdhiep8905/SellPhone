const API = "/api";

let fullOrders = [];
let currentOrder = null;

window.onload = function () {
    loadOrders();
};

// LOAD ALL ORDERS
function loadOrders() {
    fetch(`${API}/orders`)
        .then(res => res.json())
        .then(data => {
            fullOrders = data;
            renderOrders(data);
        });
}

// RENDER TABLE
function renderOrders(list) {
    let html = "";

    list.forEach(o => {
        html += `
            <tr>
                <td>${o.orderId}</td>
                <td>${o.userName}</td>
                <td>${o.bookDate}</td>
                <td>${o.totalPrice.toLocaleString()} đ</td>
                <td>${statusBadge(o.status)}</td>
                <td>
                    <button class="btn" onclick="viewOrder(${o.orderId})">Xem</button>
                </td>
            </tr>
        `;
    });

    document.querySelector("#orderTable tbody").innerHTML = html;
}

// STATUS UI BADGE
function statusBadge(status) {
    let color = {
        PENDING: "#f1c40f",
        PROCESSING: "#3498db",
        COMPLETED: "#2ecc71",
        CANCELLED: "#e74c3c"
    }[status];

    return `<span style="background:${color}; padding:5px 10px; color:white; border-radius:6px;">${status}</span>`;
}

// FILTER BY STATUS
function filterStatus(status) {
    const filtered = fullOrders.filter(o => o.status === status);
    renderOrders(filtered);
}

// VIEW ORDER DETAILS
function viewOrder(id) {
    fetch(`${API}/orders/${id}`)
        .then(res => res.json())
        .then(data => {
            currentOrder = data;

            orderId.innerText = data.orderId;
            orderUser.innerText = data.userName;
            orderDate.innerText = data.bookDate;
            orderTotal.innerText = data.totalPrice.toLocaleString() + " đ";

            orderStatus.value = data.status;

            // ITEMS
            let itemsHtml = "";
            data.items.forEach(i => {
                itemsHtml += `
                    <tr>
                        <td>${i.phoneName}</td>
                        <td>${i.quantity}</td>
                        <td>${i.price.toLocaleString()} đ</td>
                    </tr>
                `;
            });
            orderItems.innerHTML = itemsHtml;

            orderModal.style.display = "flex";
        });
}

// CLOSE MODAL
function closeModal() {
    orderModal.style.display = "none";
}

// UPDATE STATUS
function updateStatus() {
    const newStatus = orderStatus.value;

    fetch(`${API}/orders/${currentOrder.orderId}/status`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: newStatus })
    })
    .then(() => {
        alert("Cập nhật trạng thái thành công!");
        closeModal();
        loadOrders();
    });
}
function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}
