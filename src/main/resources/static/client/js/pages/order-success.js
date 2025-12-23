import { $ } from "../common/dom.js";

function getOrderIdFromUrl() {
  const params = new URLSearchParams(window.location.search);
  return params.get("orderId") || params.get("id");
}

async function fetchOrderStatus(orderId) {
  const res = await fetch(`/api/orders/${encodeURIComponent(orderId)}/status`, {
    method: "GET",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
  });

  if (!res.ok) return null;
  const data = await res.json(); // { status: "PAID" | ... }
  return data?.status || null;
}

function setAwaitingUI() {
  const steps = document.querySelector(".next-steps");
  if (!steps) return;
  steps.innerHTML = `
    <h4>Đang xác nhận thanh toán:</h4>
    <ul>
      <li><span>⏳</span> Hệ thống đang chờ xác nhận thanh toán từ ngân hàng.</li>
      <li><span>🔄</span> Trạng thái sẽ tự cập nhật sau khi thanh toán thành công.</li>
    </ul>
  `;
}

function setPaidUI() {
  const steps = document.querySelector(".next-steps");
  if (!steps) return;
  steps.innerHTML = `
    <h4>Thanh toán thành công:</h4>
    <ul>
      <li><span>✅</span> Hệ thống đã ghi nhận thanh toán cho đơn hàng.</li>
      <li><span>📦</span> Đơn hàng sẽ được xử lý và giao theo quy trình.</li>
    </ul>
  `;
}

export function initOrderSuccessPage() {
  const el = $("#display-order-id");
  if (!el) return;

  const orderId = getOrderIdFromUrl();

  if (orderId && orderId !== "undefined" && orderId !== "null") {
    el.textContent = `Mã đơn hàng: #${orderId}`;
  } else {
    el.textContent = "Mã đơn hàng: #không xác định";
    console.warn("Missing orderId in URL:", window.location.href);
    return;
  }

  // Với PayOS, cần chờ webhook set PAID
  setAwaitingUI();

  const maxAttempts = 30; // ~60s
  let attempt = 0;

  const timer = setInterval(async () => {
    attempt += 1;

    try {
      const status = await fetchOrderStatus(orderId);
      const upper = status ? String(status).toUpperCase() : "";

      if (upper === "PAID") {
        clearInterval(timer);
        setPaidUI();
        return;
      }

      if (attempt >= maxAttempts) {
        clearInterval(timer);
        console.warn("Polling timeout. Last status:", status);
      }
    } catch (e) {
      if (attempt >= maxAttempts) clearInterval(timer);
    }
  }, 2000);
}
