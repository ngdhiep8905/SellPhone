const API = "/api";

let fullOrders = [];
let currentOrder = null;

// [BỔ SUNG] Khai báo các biến DOM element (Giả định chúng tồn tại)
const orderId = document.getElementById("orderId");
const orderUser = document.getElementById("orderUser");
const orderDate = document.getElementById("orderDate");
const orderTotal = document.getElementById("orderTotal");
const orderStatus = document.getElementById("orderStatus");
const orderItems = document.getElementById("orderItems");
const orderModal = document.getElementById("orderModal");

window.onload = function () {
    // Kiểm tra đăng nhập Admin (Nếu cần)
    // if (localStorage.getItem("isAdmin") !== "true") {
    //     window.location.href = "admin-login.html";
    //     return;
    // }
    loadOrders();
};

// ======================== API FETCHING ========================

// LOAD ALL ORDERS
function loadOrders() {
    fetch(`${API}/orders`)
        .then(res => {
            if (!res.ok) throw new Error("Lỗi tải danh sách đơn hàng.");
            return res.json();
        })
        .then(data => {
            fullOrders = data;
            renderOrders(data);
        })
        .catch(err => {
            alert(err.message);
            console.error(err);
        });
}

// VIEW ORDER DETAILS
function viewOrder(id) {
    fetch(`${API}/orders/${id}`)
        .then(res => {
            if (!res.ok) throw new Error(`Không tìm thấy đơn hàng ID: ${id}`);
            return res.json();
        })
        .then(data => {
            currentOrder = data;

            // [LƯU Ý]: Giả định DTO trả về có các trường sau:
            orderId.innerText = data.orderId;
            orderUser.innerText = data.userName || data.user.fullName || "Khách hàng"; // Lấy tên người dùng
            orderDate.innerText = data.bookDate;
            orderTotal.innerText = (data.totalPrice || data.totalAmount).toLocaleString('vi-VN') + " đ";
            orderStatus.value = data.status;
            document.getElementById("recipientName").innerText = data.recipientName;
            document.getElementById("recipientPhone").innerText = data.recipientPhone;
            document.getElementById("shippingAddress").innerText = data.shippingAddress;

            // ITEMS (Giả định tên trường trong DTO là orderItems)
            let itemsHtml = "";
            (data.orderItems || []).forEach(i => {
                // [LƯU Ý]: Giả định Entity lồng có phoneName, quantity, price
                itemsHtml += `
                    <tr>
                        <td>${i.phoneName || i.phone.phoneName}</td>
                        <td>${i.quantity}</td>
                        <td>${(i.price || 0).toLocaleString('vi-VN')} đ</td>
                    </tr>
                `;
            });
            orderItems.innerHTML = itemsHtml;
            orderModal.style.display = "flex";
        })
        .catch(err => {
            alert(err.message);
            console.error(err);
        });
}

// UPDATE STATUS
function updateStatus() {
    if (!currentOrder) return;
    const newStatus = orderStatus.value;
    const statusPayload = { status: newStatus };

    fetch(`${API}/orders/${currentOrder.orderId}/status`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(statusPayload)
    })
    .then(res => {
        if (!res.ok) throw new Error("Lỗi cập nhật trạng thái đơn hàng.");
        return res.json();
    })
    .then(() => {
        alert("Cập nhật trạng thái thành công!");
        closeModal();
        loadOrders();
    })
    .catch(err => {
        alert(err.message);
        console.error(err);
    });
}

// ======================== RENDER & UI ========================

// RENDER TABLE
function renderOrders(list) {
    let html = "";

    list.forEach(o => {
        const totalDisplay = (o.totalPrice || o.totalAmount || 0).toLocaleString('vi-VN');
        const userNameDisplay = o.userName || o.userFullName || "Khách hàng";

        html += `
            <tr>
                <td>${o.orderId}</td>
                <td>${userNameDisplay}</td>
                <td>${o.bookDate}</td>
                <td>${totalDisplay} đ</td>
                <td>${statusBadge(o.status)}</td>
                <td>
                    <button class="btn" onclick="viewOrder('${o.orderId}')">Xem</button>
                </td>
            </tr>
        `;
    });

    document.querySelector("#orderTable tbody").innerHTML = html;
}

// STATUS UI BADGE
function statusBadge(status) {
    // ... (Giữ nguyên)
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

// CLOSE MODAL
function closeModal() {
    orderModal.style.display = "none";
}

function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}