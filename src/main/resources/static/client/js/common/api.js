    import { API_BASE_URL } from "./config.js";
    import { AppState, CartState, PaymentState, ProductsState } from "./state.js";
    import { requireLogin } from "./auth.js";
    import { showToast } from "./helpers.js";
    import { updateCartHeaderCount } from "./header.js";

    export async function apiFetchPayments() {
      const res = await fetch(`${API_BASE_URL}/api/payments`);
      if (!res.ok) throw new Error("Fetch payments failed");
      PaymentState.methods = (await res.json()) || [];
      return PaymentState.methods;
    }

    export async function apiLogin(email, password) {
      const url = `/api/auth/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;

      const res = await fetch(url, {
        method: "POST",
        credentials: "include", // QUAN TRỌNG: gửi cookie CART_TOKEN lên server
      });

      if (!res.ok) {
        const text = await res.text().catch(() => "");
        throw new Error(text || `HTTP ${res.status}`);
      }

      return await res.json();
    }


    export async function apiRegister(payload) {
      const res = await fetch(`${API_BASE_URL}/api/users/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      // cố gắng parse JSON cho cả trường hợp ok và lỗi
      let data = null;
      const ct = res.headers.get("content-type") || "";

      try {
        if (ct.includes("application/json")) {
          data = await res.json();
        } else {
          const text = await res.text();
          data = text ? { message: text } : null;
        }
      } catch (_) {
        data = null;
      }

      if (!res.ok) {
        // backend của bạn trả { message: "EMAIL_EXISTS", detail: "..." }
        // ném object để register.js đọc err.message và err.detail
        throw {
          status: res.status,
          message: data?.message || `HTTP_${res.status}`,
          detail: data?.detail || null,
          raw: data,
        };
      }

      return data;
    }


    export async function apiFetchProducts(keyword = "", brandId = "") {
      const params = new URLSearchParams();
      if (keyword) params.append("keyword", keyword);
      if (brandId) params.append("brandId", brandId);

      const qs = params.toString() ? `?${params.toString()}` : "";
      const url = `${API_BASE_URL}/api/phones${qs}`;
      console.log("[apiFetchProducts] GET", url);

      const res = await fetch(url);
      if (!res.ok) throw new Error("HTTP " + res.status);

      const fetchedItems = (await res.json()) || [];

      const normalized = fetchedItems.map((p) => ({
        ...p,
        phoneId: p.phoneId ?? p.id,
      }));

      ProductsState.rawItems = normalized;
      ProductsState.items = [...normalized];
      return normalized;
    }

    export async function apiAddToCart(phoneId, quantity = 1) {
      const params = new URLSearchParams({ phoneId, quantity });

      const res = await fetch(`${API_BASE_URL}/api/cart/items?${params.toString()}`, {
        method: "POST",
        credentials: "include",
      });
      if (!res.ok) throw new Error("HTTP " + res.status);

      CartState.cart = await res.json();
      updateCartHeaderCount();
      showToast("Đã thêm vào giỏ hàng.");
      return CartState.cart;
    }


    export async function apiFetchCart() {
      const res = await fetch(`${API_BASE_URL}/api/cart`, {
        credentials: "include",
      });
      if (!res.ok) throw new Error("HTTP " + res.status);

      CartState.cart = await res.json();
      updateCartHeaderCount();
      return CartState.cart;
    }


    export async function apiUpdateCartItem(cartItemId, newQuantity) {
      const res = await fetch(`${API_BASE_URL}/api/cart/items/${cartItemId}?quantity=${newQuantity}`, {
        method: "PUT",
        credentials: "include", // ✅ thêm
      });
      if (!res.ok) throw new Error("HTTP " + res.status);

      CartState.cart = await res.json();
      updateCartHeaderCount();
      return CartState.cart;
    }


    export async function apiRemoveCartItem(cartItemId) {
      const res = await fetch(`${API_BASE_URL}/api/cart/items/${cartItemId}`, {
        method: "DELETE",
        credentials: "include",
      });
      if (!res.ok) throw new Error("HTTP " + res.status);

      CartState.cart = await res.json();
      updateCartHeaderCount();
      return CartState.cart;
    }


    export async function apiCheckout(payload) {
      const res = await fetch(`${API_BASE_URL}/api/orders/checkout`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Checkout failed");
      }

      return res.json();
    }
    export async function apiLogout() {
      const res = await fetch(`/api/auth/logout`, {
        method: "POST",
        credentials: "include",
      });
      if (!res.ok) throw new Error("Logout failed");
    }


