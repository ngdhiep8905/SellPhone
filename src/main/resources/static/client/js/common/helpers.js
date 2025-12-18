import { $ } from "./dom.js";

export function formatVND(n) {
  return Number(n || 0).toLocaleString("vi-VN", { style: "currency", currency: "VND" });
}

export function getPhonePrice(p) {
  return typeof p.price === "number" ? p.price : Number(p.price || 0);
}

export function showToast(message, type = "info") {
  const toast = $("#toast");
  if (!toast) return;
  toast.textContent = message;
  toast.style.borderColor = type === "error" ? "#fb7185" : "#38bdf8";
  toast.style.backgroundColor = type === "error" ? "#991b1b" : "#020617";
  toast.classList.remove("sp-hidden");
  setTimeout(() => toast.classList.add("sp-hidden"), 2500);
}

export function getQueryParam(name) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(name);
}
