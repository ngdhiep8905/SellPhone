const API = "/api";

// Modal refs
const userModal = document.getElementById("userModal");
const modalUserId = document.getElementById("modalUserId");
const modalUserName = document.getElementById("modalUserName");
const modalUserEmail = document.getElementById("modalUserEmail");
const modalUserPhone = document.getElementById("modalUserPhone");
const modalUserAddress = document.getElementById("modalUserAddress");

window.onload = function () {
  loadUsers();
  setupBackdropClose();
};

function setupBackdropClose() {
  if (!userModal) return;
  userModal.addEventListener("click", (e) => {
    if (e.target === userModal) closeModal();
  });
}

function loadUsers() {
  // BE đã lọc chỉ role_id = '02' (khách hàng)
  fetch(`${API}/users?page=0&size=50`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(page => {
      renderUsers(page?.content || []);
    })
    .catch(err => {
      alert("Lỗi tải khách hàng: " + err.message);
      console.error(err);
    });
}

function renderUsers(users) {
  const tbody = document.querySelector("#userTable tbody");
  if (!tbody) return;

  if (!users.length) {
    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding:16px; opacity:.7;">Không có khách hàng.</td></tr>`;
    return;
  }

  let html = "";
  users.forEach(u => {
    html += `
      <tr ondblclick="viewUser('${escapeAttr(u.userId)}')">
        <td>${escapeHtml(u.userId)}</td>
        <td>${escapeHtml(u.fullName || "")}</td>
        <td>${escapeHtml(u.email || "")}</td>
        <td>${escapeHtml(u.phone || "")}</td>
        <td>${escapeHtml(u.address || "")}</td>
      </tr>
    `;
  });

  tbody.innerHTML = html;
}

function viewUser(id) {
  fetch(`${API}/users/${encodeURIComponent(id)}`)
    .then(res => {
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      return res.json();
    })
    .then(u => {
      modalUserId.innerText = u.userId || "";
      modalUserName.innerText = u.fullName || "";
      modalUserEmail.innerText = u.email || "";
      modalUserPhone.innerText = u.phone || "";
      modalUserAddress.innerText = u.address || "";
      userModal.style.display = "flex";
    })
    .catch(err => {
      alert("Lỗi xem khách hàng: " + err.message);
      console.error(err);
    });
}

function closeModal() {
  if (userModal) userModal.style.display = "none";
}

// helpers
function escapeHtml(str) {
  return String(str ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
function escapeAttr(str) {
  return String(str ?? "").replaceAll("'", "\\'");
}

function logout() {
  localStorage.removeItem("isAdmin");
  window.location.href = "admin-login.html";
}
