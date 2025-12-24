const API = "/api";

let fullOrders = [];
let currentOrderId = null;
let currentOrder = null;

// Filters
const filterOrderStatus = document.getElementById("filterOrderStatus");
const filterPaymentMethod = document.getElementById("filterPaymentMethod");
const filterPaymentStatus = document.getElementById("filterPaymentStatus");

// Modal elements
const orderModal = document.getElementById("orderModal");
const rejectModal = document.getElementById("rejectModal");

const orderIdEl = document.getElementById("orderId");
const orderUserEl = document.getElementById("orderUser");
const customerPhoneEl = document.getElementById("customerPhone");
const orderDateEl = document.getElementById("orderDate");
const orderTotalEl = document.getElementById("orderTotal");
const orderStatusTextEl = document.getElementById("orderStatusText");
const orderItemsEl = document.getElementById("orderItems");
const recipientNameEl = document.getElementById("recipientName");
const recipientPhoneEl = document.getElementById("recipientPhone");
const shippingAddressEl = document.getElementById("shippingAddress");
const paymentInfoEl = document.getElementById("paymentInfo");

const btnConfirm = document.getElementById("btnConfirm");
const btnDelivered = document.getElementById("btnDelivered");
const btnReject = document.getElementById("btnReject");

const rejectReasonEl = document.getElementById("rejectReason");
const btnSubmitReject = document.getElementById("btnSubmitReject");

window.onload = function () {
  setupBackdropClose();
  loadOrders();
};

function setupBackdropClose() {
  if (orderModal) {
    orderModal.addEventListener("click", (e) => {
      if (e.target === orderModal) closeModal();
    });
  }
  if (rejectModal) {
    rejectModal.addEventListener("click", (e) => {
      if (e.target === rejectModal) closeRejectModal();
    });
  }
}

// ========== FILTER ==========
function resetFilters() {
  if (filterOrderStatus) filterOrderStatus.value = "";
  if (filterPaymentMethod) filterPaymentMethod.value = "";
  if (filterPaymentStatus) filterPaymentStatus.value = "";
  loadOrders();
}

function buildQuery() {
  const qs = new URLSearchParams();
  if (filterOrderStatus?.value) qs.set("status", filterOrderStatus.value);
  if (filterPaymentMethod?.value) qs.set("paymentMethod", filterPaymentMethod.value);
  if (filterPaymentStatus?.value) qs.set("paymentStatus", filterPaymentStatus.value);
  return qs.toString();
}

// ========== LIST ==========
function loadOrders() {
  const q = buildQuery();
  fetch(`${API}/admin/orders${q ? "?" + q : ""}`)
    .then(res => {
      if (!res.ok) throw new Error("Lỗi tải danh sách đơn hàng.");
      return res.json();
    })
    .then(data => {
      fullOrders = data || [];
      renderOrders(fullOrders);
    })
    .catch(err => {
      alert(err.message);
      console.error(err);
    });
}

function renderOrders(list) {
  const tbody = document.querySelector("#orderTable tbody");
  if (!tbody) return;

  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="9" style="text-align:center; padding:16px; opacity:.7;">Không có đơn phù hợp.</td></tr>`;
    return;
  }

  let html = "";
  list.forEach(o => {
    const total = Number(o.totalPrice || 0).toLocaleString("vi-VN") + " đ";
    const pay = `${o.paymentMethod || ""} • ${o.paymentStatus || ""}`;
    const canConfirm = canConfirmOrder(o);           // chỉ PENDING + (COD hoặc BANKING PAID)
    const canCancel  = (o.status === "PENDING" || o.status === "SHIPPED"); // Hủy được ở 2 trạng thái
    const canDelivered = (o.status === "SHIPPED");   // chỉ SHIPPED


    html += `
      <tr ondblclick="openOrder('${o.orderId}')">
        <td>${escapeHtml(o.orderId)}</td>
        <td>${escapeHtml(o.customerName || "Khách hàng")}</td>
        <td>${escapeHtml(formatDateTime(o.bookDate))}</td>
        <td>${total}</td>
        <td>${escapeHtml(o.itemsPreview || "")}</td>
        <td>${escapeHtml(o.shippingAddress || "")}</td>
        <td>${statusBadge(o.status)}</td>
        <td>${escapeHtml(pay)}</td>        <td onclick="event.stopPropagation();">
          ${renderActions(o, canConfirm, canCancel, canDelivered)}
        </td>

      </tr>
    `;
  });

  tbody.innerHTML = html;
}
function renderActions(o, canConfirm, canCancel, canDelivered) {
  // PENDING: chỉ Xác nhận + Hủy
  if (o.status === "PENDING") {
    return `
      <button class="btn" ${canConfirm ? "" : "disabled"} onclick="confirmOrder('${escapeAttr(o.orderId)}')">Xác nhận</button>
      <button class="btn" style="background:#d63031" ${canCancel ? "" : "disabled"} onclick="openReject('${escapeAttr(o.orderId)}')">Hủy</button>
    `;
  }

  // SHIPPED: chỉ Giao thành công + Hủy
  if (o.status === "SHIPPED") {
    return `
      <button class="btn" ${canDelivered ? "" : "disabled"} onclick="deliveredOrder('${escapeAttr(o.orderId)}')">Giao thành công</button>
      <button class="btn" style="background:#d63031" ${canCancel ? "" : "disabled"} onclick="openReject('${escapeAttr(o.orderId)}')">Hủy</button>
    `;
  }

  // DELIVERED / CANCELLED: không còn nút
  return `<span style="opacity:.7;">-</span>`;
}


// Rules
function canConfirmOrder(o) {
  if (o.status !== "PENDING") return false;
  if (o.paymentMethod === "COD") return true;
  if (o.paymentMethod === "BANKING" && o.paymentStatus === "PAID") return true;
  return false;
}

// Status UI
function statusBadge(status) {
  const color = {
    PENDING: "#f1c40f",
    SHIPPED: "#3498db",
    DELIVERED: "#2ecc71",
    CANCELLED: "#e74c3c",
    PROCESSING: "#9b59b6"
  }[status] || "#64748b";

  const label = {
    PENDING: "Cần xác nhận",
    SHIPPED: "Đã xác nhận, đang vận chuyển",
    DELIVERED: "Giao thành công",
    CANCELLED: "Đã hủy",
    PROCESSING: "Đang xử lý"
  }[status] || status;

  return `<span style="background:${color}; padding:5px 10px; color:white; border-radius:6px;">${label}</span>`;
}

function formatDate(v) {
  // bookDate là LocalDate => thường trả về "YYYY-MM-DD"
  return v ? String(v) : "";
}

// ========== DETAIL ==========
function openOrder(id) {
  fetch(`${API}/admin/orders/${encodeURIComponent(id)}`)
    .then(res => {
      if (!res.ok) throw new Error(`Không tìm thấy đơn: ${id}`);
      return res.json();
    })
    .then(data => {
      currentOrder = data;
      currentOrderId = data.orderId;

      orderIdEl.innerText = data.orderId;
      orderUserEl.innerText = data.customerName || "Khách hàng";
      if (customerPhoneEl) customerPhoneEl.innerText = data.customerPhone || "";
      orderDateEl.innerText = formatDateTime(data.bookDate);
      orderTotalEl.innerText = Number(data.totalPrice || 0).toLocaleString("vi-VN") + " đ";

      if (recipientNameEl) recipientNameEl.innerText = data.recipientName || "";
      if (recipientPhoneEl) recipientPhoneEl.innerText = data.recipientPhone || "";
      if (shippingAddressEl) shippingAddressEl.innerText = data.shippingAddress || "";

      if (paymentInfoEl) paymentInfoEl.innerHTML = paymentBadge(data.paymentMethod, data.paymentStatus);

      if (orderStatusTextEl) orderStatusTextEl.innerText = mapStatusText(data.status);

      // items
      let itemsHtml = "";
      (data.items || []).forEach(i => {
        itemsHtml += `
          <tr>
            <td>${escapeHtml(i.phoneName || i.phoneId)}</td>
            <td>${i.quantity}</td>
            <td>${Number(i.price || 0).toLocaleString("vi-VN")} đ</td>
          </tr>
        `;
      });
      orderItemsEl.innerHTML = itemsHtml;

      // modal buttons
      syncModalButtons(data);


      orderModal.style.display = "flex";
    })
    .catch(err => {
      alert(err.message);
      console.error(err);
    });
}

function mapStatusText(s) {
  return {
    PENDING: "Cần xác nhận",
    SHIPPED: "Đã xác nhận, đang vận chuyển",
    DELIVERED: "Giao thành công",
    CANCELLED: "Đã hủy"
  }[s] || s || "";
}

function closeModal() {
  orderModal.style.display = "none";
}

// ========== ACTIONS ==========
function confirmOrder(id, fromModal = false) {
  if (!confirm(`Xác nhận đơn ${id}?`)) return;

  fetch(`${API}/admin/orders/${encodeURIComponent(id)}/confirm`, { method: "PUT" })
    .then(res => res.ok ? null : res.text().then(t => { throw new Error(t || "Lỗi xác nhận đơn."); }))
    .then(() => {
      loadOrders();                 // cập nhật bảng list
      if (fromModal) openOrder(id); // refresh modal: PENDING -> SHIPPED => đổi nút ngay
    })
    .catch(err => alert(err.message));
}

function deliveredOrder(id, fromModal = false) {
  if (!confirm(`Xác nhận giao thành công đơn ${id}?`)) return;

  fetch(`${API}/admin/orders/${encodeURIComponent(id)}/delivered`, { method: "PUT" })
    .then(res => res.ok ? null : res.text().then(t => { throw new Error(t || "Lỗi cập nhật giao thành công."); }))
    .then(() => {
      loadOrders();                 // cập nhật bảng list
      if (fromModal) openOrder(id); // refresh modal: SHIPPED -> DELIVERED => hết nút
    })
    .catch(err => alert(err.message));
}

// reject flow (HỦY)
function openReject(id, fromModal = false) {
  if (!rejectModal || !rejectReasonEl || !btnSubmitReject) {
    alert("Thiếu rejectModal/rejectReason/btnSubmitReject trong HTML");
    return;
  }

  currentOrderId = id;
  rejectReasonEl.value = "";
  rejectModal.style.display = "flex";

  // đổi label cho đúng nghiệp vụ "Hủy"
  if (btnSubmitReject) btnSubmitReject.innerText = "Xác nhận hủy";

  btnSubmitReject.onclick = () => submitReject(id, rejectReasonEl.value, fromModal);
}

function closeRejectModal() {
  if (rejectModal) rejectModal.style.display = "none";
}

function submitReject(id, reason, fromModal = false) {
  const r = (reason || "").trim();
  if (!r) return alert("Vui lòng nhập lý do hủy.");

  fetch(`${API}/admin/orders/${encodeURIComponent(id)}/reject`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ reason: r })
  })
    .then(res => res.ok ? null : res.text().then(t => { throw new Error(t || "Lỗi hủy đơn."); }))
    .then(() => {
      closeRejectModal();
      loadOrders();                 // cập nhật bảng list
      if (fromModal) openOrder(id); // refresh modal: -> CANCELLED => hết nút
    })
    .catch(err => alert(err.message));
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
function formatDateTime(v) {
  if (!v) return "";
  const d = new Date(v);
  if (isNaN(d.getTime())) return String(v);
  return d.toLocaleString("vi-VN");
}
function syncModalButtons(data) {
  const canConfirm = (data.status === "PENDING")
    && (data.paymentMethod === "COD" || (data.paymentMethod === "BANKING" && data.paymentStatus === "PAID"));

  const canCancel = (data.status === "PENDING" || data.status === "SHIPPED");
  const canDelivered = (data.status === "SHIPPED");

  // Ẩn hết trước
  if (btnConfirm) btnConfirm.style.display = "none";
  if (btnDelivered) btnDelivered.style.display = "none";
  if (btnReject) btnReject.style.display = "none";

  // PENDING: Xác nhận + Hủy
  if (data.status === "PENDING") {
    if (btnConfirm) {
      btnConfirm.style.display = "inline-block";
      btnConfirm.disabled = !canConfirm;
      btnConfirm.onclick = () => confirmOrder(data.orderId, true);
    }
    if (btnReject) {
      btnReject.style.display = "inline-block";
      btnReject.disabled = !canCancel; // luôn true ở PENDING
      btnReject.innerText = "Hủy";
      btnReject.onclick = () => openReject(data.orderId, true);
    }
    return;
  }

  // SHIPPED: Giao thành công + Hủy
  if (data.status === "SHIPPED") {
    if (btnDelivered) {
      btnDelivered.style.display = "inline-block";
      btnDelivered.disabled = !canDelivered; // luôn true ở SHIPPED
      btnDelivered.onclick = () => deliveredOrder(data.orderId, true);
    }
    if (btnReject) {
      btnReject.style.display = "inline-block";
      btnReject.disabled = !canCancel; // true ở SHIPPED
      btnReject.innerText = "Hủy";
      btnReject.onclick = () => openReject(data.orderId, true);
    }
    return;
  }

  // DELIVERED / CANCELLED: không nút nào
}
function paymentBadge(method, status) {
  if (!method) return "";

  const isPaid = status === "PAID";

  const color = isPaid ? "#2ecc71" : "#f1c40f";
  const label = isPaid ? "Đã thanh toán" : "Chưa thanh toán";

  return `
    <span style="
      background:${color};
      padding:5px 10px;
      color:white;
      border-radius:6px;
      font-weight:600;
      display:inline-block;
      min-width:120px;
      text-align:center;
    ">
      ${method} • ${label}
    </span>
  `;
}

