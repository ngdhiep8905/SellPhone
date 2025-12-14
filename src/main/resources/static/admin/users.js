const API = "http://localhost:8080/api";

let currentUser = null;

// LOAD USERS
window.onload = function () {
    loadUsers();
};

function loadUsers() {
    fetch(`${API}/users`)
        .then(res => res.json())
        .then(data => renderUsers(data));
}

function renderUsers(users) {
    let html = "";

    users.forEach(u => {
        html += `
            <tr>
                <td>${u.userId}</td>
                <td>${u.fullName}</td>
                <td>${u.email}</td>
                <td>${roleBadge(u.role)}</td>
                <td>${statusBadge(u.status)}</td>
                <td>
                    <button class="btn" onclick="viewUser(${u.userId})">Xem</button>
                </td>
            </tr>
        `;
    });

    document.querySelector("#userTable tbody").innerHTML = html;
}

function roleBadge(role) {
    const colors = {
        "ADMIN": "#8e44ad",
        "USER": "#3498db"
    };
    return `<span style="background:${colors[role]}; padding:6px 12px; color:white; border-radius:8px;">${role}</span>`;
}

function statusBadge(status) {
    const colors = {
        "ACTIVE": "#2ecc71",
        "LOCKED": "#e74c3c"
    };
    const text = {
        "ACTIVE": "Hoạt động",
        "LOCKED": "Đã khóa"
    };
    return `<span style="background:${colors[status]}; padding:6px 12px; color:white; border-radius:8px;">${text[status]}</span>`;
}

// VIEW USER
function viewUser(id) {
    fetch(`${API}/users/${id}`)
        .then(res => res.json())
        .then(u => {
            currentUser = u;

            modalUserId.innerText = u.userId;
            modalUserName.innerText = u.fullName;
            modalUserEmail.innerText = u.email;
            modalUserRole.innerText = u.role;
            modalUserStatus.innerText = u.status === "ACTIVE" ? "Hoạt động" : "Đã khóa";

            userModal.style.display = "flex";
        });
}

function closeModal() {
    userModal.style.display = "none";
}

// TOGGLE USER STATUS (LOCK/UNLOCK)
function toggleUserStatus() {
    const newStatus = currentUser.status === "ACTIVE" ? "LOCKED" : "ACTIVE";

    fetch(`${API}/users/${currentUser.userId}/status`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ status: newStatus })
    })
    .then(() => {
        alert("Cập nhật trạng thái thành công!");
        closeModal();
        loadUsers();
    });
}
function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}
