// ============ CONFIG ============
const API_BASE_URL = ""; // cùng host với Spring Boot, dùng path tương đối

// ============ GLOBAL STATE ============
const AppState = {
  currentUser: null,
  currentDetailPhone: null,
};

const ProductsState = {
  rawItems: [], // toàn bộ dữ liệu từ API
  items: [], // danh sách sau khi áp dụng filter
  currentPage: 1,
  pageSize: 8,
  brandId: "",
  keyword: "",
  priceFilter: "",
  sort: "",
};

const CartState = {
  cart: null,
};
const PaymentState = {
  methods: [],
};

// ============ HELPERS ============
const $ = (sel) => document.querySelector(sel);

function formatVND(n) {
  return Number(n || 0).toLocaleString("vi-VN", {
    style: "currency",
    currency: "VND",
  });
}

// lấy giá từ object phone (tránh lỗi kiểu chuỗi)
function getPhonePrice(p) {
  return typeof p.price === "number" ? p.price : Number(p.price || 0);
}

function showToast(message, type = "info") {
  const toast = $("#toast");
  if (!toast) return;
  toast.textContent = message;
  toast.style.borderColor = type === "error" ? "#fb7185" : "#38bdf8";
  toast.style.backgroundColor = type === "error" ? "#991b1b" : "#020617";
  toast.classList.remove("sp-hidden");
  setTimeout(() => toast.classList.add("sp-hidden"), 2500);
}

function getQueryParam(name) {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get(name);
}

// localStorage user
function loadUser() {
  try {
    const raw = localStorage.getItem("sellphone_user");
    if (!raw) return;
    const u = JSON.parse(raw);
    if (u && u.userId) {
      AppState.currentUser = u;
    }
  } catch {
    // ignore
  }
}

function saveUser() {
  if (AppState.currentUser) {
    localStorage.setItem(
      "sellphone_user",
      JSON.stringify(AppState.currentUser)
    );
  } else {
    localStorage.removeItem("sellphone_user");
  }
}

function requireLogin(redirectPage) {
  if (!AppState.currentUser) {
    const target =
      redirectPage ||
      window.location.pathname.replace(/^\//, "") ||
      "index.html";
    window.location.href = "login.html?redirect=" + encodeURIComponent(target);
    return false;
  }
  return true;
}

// ============ CART HEADER COUNT ============
function updateCartHeaderCount() {
  const headerCountEl = $("#cart-header-count");
  if (!headerCountEl) return;

  if (!CartState.cart || !CartState.cart.items) {
    headerCountEl.textContent = "0";
    return;
  }

  let totalQty = 0;
  CartState.cart.items.forEach((item) => {
    const q =
      item.quantityPrice != null ? item.quantityPrice : item.quantity || 0;
    totalQty += q;
  });

  headerCountEl.textContent = totalQty;
}

// ============ HEADER UI ============
function updateHeaderUI() {
  const label = $("#user-label");
  const loginBtn = $("#login-header-btn");
  const logoutBtn = $("#logout-header-btn");

  if (!label || !loginBtn || !logoutBtn) return;

  if (AppState.currentUser) {
    label.textContent = `Xin chào, ${
      AppState.currentUser.userName || AppState.currentUser.email
    }`;
    loginBtn.classList.add("sp-hidden");
    logoutBtn.classList.remove("sp-hidden");
  } else {
    label.textContent = "Chưa đăng nhập";
    loginBtn.classList.remove("sp-hidden");
    logoutBtn.classList.add("sp-hidden");
  }

  loginBtn.onclick = () => {
    window.location.href = "login.html";
  };

  logoutBtn.onclick = () => {
    AppState.currentUser = null;
    CartState.cart = null;
    saveUser();
    updateHeaderUI();
    updateCartHeaderCount();
    showToast("Đã đăng xuất.");
    const page = document.body.dataset.page;
    if (["cart", "account"].includes(page)) {
      window.location.href = "index.html";
    }
  };
}

// ============ API LAYER ============
async function apiFetchPayments() {
  const res = await fetch(`${API_BASE_URL}/api/payments`);
  if (!res.ok) {
    let msg = "Fetch payments failed";
    try {
      const err = await res.json();
      if (err && err.message) msg = err.message;
    } catch {}
    throw new Error(msg);
  }
  PaymentState.methods = (await res.json()) || [];
  return PaymentState.methods;
}

function renderPaymentOptions() {
  const sel = $("#checkout-payment");
  if (!sel) return;

  sel.innerHTML = "";

  (PaymentState.methods || []).forEach((p) => {
    // Backend Payment thường có: paymentId, paymentMethod, paymentStatus
    const id = p.paymentId ?? p.id ?? p.payment_id;
    const name = p.paymentMethod ?? p.method ?? "Thanh toán";

    const opt = document.createElement("option");
    opt.value = String(id);
    opt.textContent = name;
    sel.appendChild(opt);
  });
}

async function apiLogin(email, password) {
  const params = new URLSearchParams({ email, password });
  const res = await fetch(
    `${API_BASE_URL}/api/users/login?${params.toString()}`,
    { method: "POST" }
  );

  if (!res.ok) {
    // Đọc thêm message từ backend nếu có
    let msg = "Login failed";
    try {
      const err = await res.json();
      if (err && err.message) msg = err.message;
    } catch {
      // body không phải JSON thì bỏ qua
    }
    throw new Error(msg);
  }

  return res.json(); // 200 OK + body là JSON Users → không còn lỗi JSON nữa
}

async function apiRegister(payload) {
  const res = await fetch(`${API_BASE_URL}/api/users/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    let msg = "Register failed";
    try {
      const err = await res.json();
      if (err && err.message) msg = err.message;
    } catch {
      // ignore
    }
    throw new Error(msg);
  }

  return res.json(); // trả về Users
}




// chỉ gọi 1 lần, lấy toàn bộ danh sách phone
async function apiFetchPhones() {
  const loadingEl = $("#product-loading");
  const errorEl = $("#product-error");
  if (loadingEl) loadingEl.classList.remove("sp-hidden");
  if (errorEl) errorEl.classList.add("sp-hidden");

  try {
    const res = await fetch(`${API_BASE_URL}/api/phones`);
    if (!res.ok) throw new Error("HTTP " + res.status);
    ProductsState.rawItems = (await res.json()) || [];
    ProductsState.currentPage = 1;
    applyProductFilters();
  } catch (err) {
    console.error(err);
    if (errorEl) {
      errorEl.textContent =
        "Không tải được danh sách điện thoại. Kiểm tra API /api/phones.";
      errorEl.classList.remove("sp-hidden");
    }
  } finally {
    if (loadingEl) loadingEl.classList.add("sp-hidden");
  }
}

async function apiAddToCart(phoneId, quantity = 1) {
  if (!requireLogin("products.html")) return;

  const errorEl = $("#cart-error");
  if (errorEl) errorEl.classList.add("sp-hidden");

  const params = new URLSearchParams({
    userId: AppState.currentUser.userId,
    phoneId,
    quantity,
  });

  try {
    const res = await fetch(
      `${API_BASE_URL}/api/cart/items?${params.toString()}`,
      { method: "POST" }
    );
    if (!res.ok) throw new Error("HTTP " + res.status);
    CartState.cart = await res.json();
    updateCartHeaderCount();
    showToast("Đã thêm vào giỏ hàng. Xem chi tiết tại trang Giỏ hàng.");
  } catch (err) {
    console.error(err);
    if (errorEl) {
      errorEl.textContent = "Không thêm được sản phẩm vào giỏ.";
      errorEl.classList.remove("sp-hidden");
    } else {
      showToast("Không thêm được sản phẩm vào giỏ.", "error");
    }
  }
}

async function apiFetchCart() {
  if (!AppState.currentUser) {
    CartState.cart = null;
    updateCartHeaderCount();
    renderCartPage();
    return;
  }
  const errorEl = $("#cart-error");
  if (errorEl) errorEl.classList.add("sp-hidden");

  try {
    const res = await fetch(
      `${API_BASE_URL}/api/cart/${AppState.currentUser.userId}`
    );
    if (!res.ok) throw new Error("HTTP " + res.status);
    CartState.cart = await res.json();
    updateCartHeaderCount();
    renderCartPage();
  } catch (err) {
    console.error(err);
    if (errorEl) {
      errorEl.textContent =
        "Không lấy được giỏ hàng từ backend. Kiểm tra API /api/cart/{userId}.";
      errorEl.classList.remove("sp-hidden");
    }
  }
}

async function apiUpdateCartItem(cartItemId, newQuantity) {
  const errorEl = $("#cart-error");
  if (errorEl) errorEl.classList.add("sp-hidden");

  try {
    const res = await fetch(
      `${API_BASE_URL}/api/cart/items/${cartItemId}?quantity=${newQuantity}`,
      { method: "PUT" }
    );
    if (!res.ok) throw new Error("HTTP " + res.status);
    CartState.cart = await res.json();
    updateCartHeaderCount();
    renderCartPage();
  } catch (err) {
    console.error(err);
    if (errorEl) {
      errorEl.textContent = "Không cập nhật được số lượng.";
      errorEl.classList.remove("sp-hidden");
    }
  }
}

async function apiRemoveCartItem(cartItemId) {
  const errorEl = $("#cart-error");
  if (errorEl) errorEl.classList.add("sp-hidden");

  try {
    const res = await fetch(`${API_BASE_URL}/api/cart/items/${cartItemId}`, {
      method: "DELETE",
    });
    if (!res.ok) throw new Error("HTTP " + res.status);
    CartState.cart = await res.json();
    updateCartHeaderCount();
    renderCartPage();
  } catch (err) {
    console.error(err);
    if (errorEl) {
      errorEl.textContent = "Không xoá được sản phẩm khỏi giỏ.";
      errorEl.classList.remove("sp-hidden");
    }
  }
}

async function apiCheckout(payload) {
  const {
    userId,
    receiverName,
    receiverAddress,
    receiverPhone,
    couponCode,
    paymentId,
  } = payload;

  const params = new URLSearchParams({
    userId,
    receiverName,
    receiverAddress,
    receiverPhone,
    couponCode: couponCode || "",
    paymentId: String(paymentId || ""),
  });

  const res = await fetch(
    `${API_BASE_URL}/api/orders/checkout?${params.toString()}`,
    { method: "POST" }
  );

  if (!res.ok) {
    let msg = "Checkout failed";
    try {
      const err = await res.json();
      if (err && err.message) msg = err.message;
    } catch {}
    throw new Error(msg);
  }

  return res.json();
}


// ============ PRODUCT FILTERING ============
function applyProductFilters() {
  let list = [...(ProductsState.rawItems || [])];

  // từ khoá
  if (ProductsState.keyword) {
    const kw = ProductsState.keyword.toLowerCase();
    list = list.filter((p) => {
      const name = (p.phoneName || "").toLowerCase();
      const desc = (p.phoneDescription || "").toLowerCase();
      return name.includes(kw) || desc.includes(kw);
    });
  }

  // hãng (ưu tiên brandId)
  if (ProductsState.brandId) {
    list = list.filter((p) => {
      if (p.brandId != null) {
        return String(p.brandId) === ProductsState.brandId;
      }
      // fallback theo tên
      if (!p.brandName) return false;
      const map = {
        1: "apple",
        2: "samsung",
        3: "xiaomi",
        4: "oppo",
      };
      const target = map[ProductsState.brandId];
      return p.brandName.toLowerCase().includes(target);
    });
  }

  // lọc khoảng giá
  if (ProductsState.priceFilter) {
    list = list.filter((p) => {
      const price = getPhonePrice(p);
      switch (ProductsState.priceFilter) {
        case "under5":
          return price < 5_000_000;
        case "5to10":
          return price >= 5_000_000 && price <= 10_000_000;
        case "10to20":
          return price > 10_000_000 && price <= 20_000_000;
        case "over20":
          return price > 20_000_000;
        default:
          return true;
      }
    });
  }

  // sắp xếp
  if (ProductsState.sort) {
    const sort = ProductsState.sort;
    list.sort((a, b) => {
      if (sort === "priceAsc") {
        return getPhonePrice(a) - getPhonePrice(b);
      }
      if (sort === "priceDesc") {
        return getPhonePrice(b) - getPhonePrice(a);
      }
      if (sort === "nameAsc") {
        return (a.phoneName || "").localeCompare(b.phoneName || "");
      }
      return 0;
    });
  }

  ProductsState.items = list;
  ProductsState.currentPage = 1;
  renderProducts();
}

// ============ PRODUCTS RENDER ============
function renderProducts() {
  const container = $("#product-list");
  const countEl = $("#product-count");
  const pagEl = $("#pagination");

  if (!container) return;

  container.innerHTML = "";
  if (countEl) countEl.textContent = "";
  if (pagEl) pagEl.innerHTML = "";

  if (!ProductsState.items.length) {
    container.innerHTML =
      "<p class='sp-text--muted'>Không tìm thấy sản phẩm nào với bộ lọc hiện tại.</p>";
    return;
  }

  const totalPages =
    Math.ceil(ProductsState.items.length / ProductsState.pageSize) || 1;
  if (ProductsState.currentPage > totalPages)
    ProductsState.currentPage = totalPages;

  const start = (ProductsState.currentPage - 1) * ProductsState.pageSize;
  const pageItems = ProductsState.items.slice(
    start,
    start + ProductsState.pageSize
  );

  if (countEl) {
    countEl.textContent = `Hiển thị ${pageItems.length}/${ProductsState.items.length} sản phẩm`;
  }

  pageItems.forEach((p) => {
    const card = document.createElement("article");
    card.className = "sp-product-card";

    const price = getPhonePrice(p);

    const imgHtml = p.coverImageURL
      ? `<img src="${p.coverImageURL}" alt="${p.phoneName}">`
      : "Hình minh hoạ";

    const desc = p.phoneDescription || "Chưa có mô tả.";

    card.innerHTML = `
      <div class="sp-product-card__image">
        ${imgHtml}
      </div>
      <div class="sp-product-card__title">${p.phoneName}</div>
      <div class="sp-product-card__price">${formatVND(price)}</div>
      <div class="sp-product-card__desc">${desc}</div>
      <div class="sp-product-card__footer">
        <button class="sp-btn sp-btn--primary sp-btn--sm" data-action="add" data-id="${
          p.id
        }">
          Thêm vào giỏ
        </button>
        <button class="sp-btn sp-btn--outline sp-btn--sm" data-action="detail" data-id="${
          p.id
        }">
          Xem chi tiết
        </button>
      </div>
    `;
    container.appendChild(card);
  });

  if (pagEl && totalPages > 1) {
    pagEl.innerHTML = `
      <button class="sp-page-btn" id="page-prev" ${
        ProductsState.currentPage === 1 ? "disabled" : ""
      }>←</button>
      <span>Trang ${ProductsState.currentPage} / ${totalPages}</span>
      <button class="sp-page-btn" id="page-next" ${
        ProductsState.currentPage === totalPages ? "disabled" : ""
      }>→</button>
    `;
    $("#page-prev").onclick = () => {
      if (ProductsState.currentPage > 1) {
        ProductsState.currentPage--;
        renderProducts();
      }
    };
    $("#page-next").onclick = () => {
      if (ProductsState.currentPage < totalPages) {
        ProductsState.currentPage++;
        renderProducts();
      }
    };
  }
}

// ============ PRODUCT DETAIL MODAL ============
function openDetailModal(phoneId) {
  const modal = $("#detail-modal");
  if (!modal) return;

  const phone = ProductsState.items.find((p) => p.id === phoneId);
  if (!phone) return;
  AppState.currentDetailPhone = phone;

  $("#detail-name").textContent = phone.phoneName || "";
  $("#detail-price").textContent = formatVND(getPhonePrice(phone));
  $("#detail-desc").textContent =
    phone.phoneDescription || "Chưa có mô tả chi tiết.";
  $("#detail-brand").textContent = phone.brandName || "Không rõ";
  $("#detail-storage").textContent = phone.storage || "Không rõ";

  modal.classList.remove("sp-hidden");
}

function closeDetailModal() {
  const modal = $("#detail-modal");
  if (!modal) return;
  AppState.currentDetailPhone = null;
  modal.classList.add("sp-hidden");
}

// ============ CART RENDER (TRANG cart.html) ============
function renderCartPage() {
  const itemsEl = $("#cart-items");
  const countEl = $("#cart-count");
  const subtotalEl = $("#cart-subtotal");
  const shippingEl = $("#cart-shipping");
  const totalEl = $("#cart-total");
  const emptyEl = $("#cart-empty");

  if (!itemsEl) return;

  if (
    !CartState.cart ||
    !CartState.cart.items ||
    !CartState.cart.items.length
  ) {
    itemsEl.innerHTML = "";
    if (emptyEl) emptyEl.classList.remove("sp-hidden");
    if (countEl) countEl.textContent = "0 sp";
    if (subtotalEl) subtotalEl.textContent = "0₫";
    if (shippingEl) shippingEl.textContent = "0₫";
    if (totalEl) totalEl.textContent = "0₫";
    return;
  }

  if (emptyEl) emptyEl.classList.add("sp-hidden");
  itemsEl.innerHTML = "";

  let totalQty = 0;
  let subtotal = 0;

  CartState.cart.items.forEach((item) => {
    const phone = item.phone || {};
    const qty =
      item.quantityPrice != null ? item.quantityPrice : item.quantity || 0;
    const price = getPhonePrice(phone);
    const lineTotal = qty * price;

    totalQty += qty;
    subtotal += lineTotal;

    const row = document.createElement("div");
    row.className = "sp-cart-item";
    row.innerHTML = `
      <div class="sp-cart-item__row">
        <span>${phone.phoneName || "Điện thoại"}</span>
        <span>${formatVND(lineTotal)}</span>
      </div>
      <div class="sp-cart-item__row">
        <small>${phone.phoneDescription || ""}</small>
        <div class="sp-cart-item__controls">
          <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="minus" data-id="${
            item.cartItemId
          }">-</button>
          <span>${qty}</span>
          <button class="sp-btn sp-btn--outline sp-btn--icon" data-cart-action="plus" data-id="${
            item.cartItemId
          }">+</button>
          <button class="sp-btn sp-btn--ghost sp-btn--sm" data-cart-action="remove" data-id="${
            item.cartItemId
          }">X</button>
        </div>
      </div>
    `;
    itemsEl.appendChild(row);
  });

  const shipping = subtotal > 0 ? 30000 : 0;
  const total = subtotal + shipping;

  if (countEl) countEl.textContent = `${totalQty} sp`;
  if (subtotalEl) subtotalEl.textContent = formatVND(subtotal);
  if (shippingEl) shippingEl.textContent = formatVND(shipping);
  if (totalEl) totalEl.textContent = formatVND(total);
}

// ============ PAGE INIT: LOGIN ============
function initLoginPage() {
  const emailInput = $("#login-email");
  const pwdInput = $("#login-password");
  const submitBtn = $("#login-submit");
  const cancelBtn = $("#login-cancel");
  const errorEl = $("#login-error");

  if (!emailInput || !pwdInput || !submitBtn) return;

  const redirect = getQueryParam("redirect");

  async function handleLogin() {
    const email = emailInput.value.trim();
    const password = pwdInput.value.trim();
    if (!email || !password) {
      errorEl.textContent = "Vui lòng nhập đầy đủ email và mật khẩu.";
      errorEl.classList.remove("sp-hidden");
      return;
    }
    errorEl.classList.add("sp-hidden");
    try {
      const user = await apiLogin(email, password);
      AppState.currentUser = user;
      saveUser();
      updateHeaderUI();
      showToast("Đăng nhập thành công.");
      const target = redirect || "products.html";
      window.location.href = target;
    } catch (err) {
      console.error(err);
      errorEl.textContent =
        "Đăng nhập thất bại. Kiểm tra lại email/mật khẩu hoặc backend.";
      errorEl.classList.remove("sp-hidden");
    }
  }

  submitBtn.onclick = handleLogin;
  pwdInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") handleLogin();
  });
  if (cancelBtn) {
    cancelBtn.onclick = () => {
      window.location.href = "index.html";
    };
  }
}

// ============ PAGE INIT: REGISTER ============
function initRegisterPage() {
  const usernameInput = $("#reg-username");
  const fullNameInput = $("#reg-fullname");
  const emailInput = $("#reg-email");
  const phoneInput = $("#reg-phone");
  const addrInput = $("#reg-address");
  const pwdInput = $("#reg-password");
  const submitBtn = $("#reg-submit");
  const cancelBtn = $("#reg-cancel");
  const errorEl = $("#reg-error");
  const successEl = $("#reg-success");
  const paymentSelect = $("#checkout-payment");


  if (!usernameInput || !emailInput || !pwdInput || !submitBtn) return;

  async function handleRegister() {
    const userName = usernameInput.value.trim();
    const fullName = fullNameInput.value.trim();
    const email = emailInput.value.trim();
    const phone = phoneInput.value.trim();
    const address = addrInput.value.trim();
    const password = pwdInput.value.trim();

    errorEl.classList.add("sp-hidden");
    successEl.classList.add("sp-hidden");

    if (!userName || !email || !password) {
      errorEl.textContent =
        "Vui lòng nhập ít nhất Tên đăng nhập, Email và Mật khẩu.";
      errorEl.classList.remove("sp-hidden");
      return;
    }

    try {
      const user = await apiRegister({
        userName,
        fullName,
        email,
        phone,
        address,
        password,
      });

      // Tự động đăng nhập sau khi đăng ký
      AppState.currentUser = user;
      saveUser();
      updateHeaderUI();
      showToast("Đăng ký thành công. Đã đăng nhập.");

      successEl.textContent = "Đăng ký thành công!";
      successEl.classList.remove("sp-hidden");

      // chuyển sang trang products
      window.location.href = "products.html";
    } catch (err) {
      console.error(err);
      errorEl.textContent =
        err.message === "EMAIL_EXISTS"
          ? "Email đã tồn tại. Vui lòng dùng email khác."
          : "Đăng ký thất bại. Vui lòng thử lại.";
      errorEl.classList.remove("sp-hidden");
    }
  }

  submitBtn.onclick = handleRegister;
  pwdInput.addEventListener("keypress", (e) => {
    if (e.key === "Enter") handleRegister();
  });
  if (cancelBtn) {
    cancelBtn.onclick = () => {
      window.location.href = "login.html";
    };
  }
}


// ============ PAGE INIT: PRODUCTS ============
function initProductsPage() {
  const searchInput = $("#search-input");
  const clearBtn = $("#clear-search-btn");
  const brandSelect = $("#brand-filter");
  const priceSelect = $("#price-filter");
  const sortSelect = $("#sort-filter");
  const listEl = $("#product-list");
  const detailCloseEls = document.querySelectorAll(
    '[data-close-modal="detail"]'
  );
  const detailAddBtn = $("#detail-add");

  if (!listEl) return;

  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      ProductsState.keyword = e.target.value.trim();
      applyProductFilters();
    });
  }

  if (clearBtn) {
    clearBtn.onclick = () => {
      searchInput.value = "";
      ProductsState.keyword = "";
      applyProductFilters();
    };
  }

  if (brandSelect) {
    brandSelect.addEventListener("change", (e) => {
      ProductsState.brandId = e.target.value || "";
      applyProductFilters();
    });
  }

  if (priceSelect) {
    priceSelect.addEventListener("change", (e) => {
      ProductsState.priceFilter = e.target.value || "";
      applyProductFilters();
    });
  }

  if (sortSelect) {
    sortSelect.addEventListener("change", (e) => {
      ProductsState.sort = e.target.value || "";
      applyProductFilters();
    });
  }

  listEl.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-action]");
    if (!btn) return;
    const id = Number(btn.dataset.id);
    const action = btn.dataset.action;
    if (action === "add") {
      apiAddToCart(id, 1);
    } else if (action === "detail") {
      openDetailModal(id);
    }
  });

  detailCloseEls.forEach((el) => {
    el.addEventListener("click", closeDetailModal);
  });

  if (detailAddBtn) {
    detailAddBtn.onclick = () => {
      if (AppState.currentDetailPhone) {
        apiAddToCart(AppState.currentDetailPhone.id, 1);
        closeDetailModal();
      }
    };
  }

  apiFetchPhones();
  if (AppState.currentUser) {
    apiFetchCart();
  } else {
    updateCartHeaderCount();
  }
}

// ============ PAGE INIT: CART ============
function initCartPage() {
  if (!requireLogin("cart.html")) {
    updateCartHeaderCount();
    return;
  }

  const itemsEl = $("#cart-items");
  const checkoutBtn = $("#checkout-submit");
  const errorEl = $("#checkout-error");
  const successEl = $("#checkout-success");

  if (!itemsEl || !checkoutBtn) return;
    // Load payment methods cho dropdown
  if (paymentSelect) {
    apiFetchPayments()
      .then(() => {
        renderPaymentOptions();
        // auto-select option đầu nếu có
        if (paymentSelect.options.length > 0) paymentSelect.selectedIndex = 0;
      })
      .catch((err) => {
        console.error(err);
        if (errorEl) {
          errorEl.textContent =
            "Không tải được phương thức thanh toán. Kiểm tra API /api/payments.";
          errorEl.classList.remove("sp-hidden");
        }
      });
  }


  itemsEl.addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-cart-action]");
    if (!btn) return;
    const cartItemId = Number(btn.dataset.id);
    const action = btn.dataset.cartAction;

    const current = CartState.cart?.items?.find(
      (i) => i.cartItemId === cartItemId
    );
    if (!current) return;
    const qty =
      current.quantityPrice != null
        ? current.quantityPrice
        : current.quantity || 0;

    if (action === "plus") {
      apiUpdateCartItem(cartItemId, qty + 1);
    } else if (action === "minus") {
      const newQty = qty - 1;
      if (newQty <= 0) apiRemoveCartItem(cartItemId);
      else apiUpdateCartItem(cartItemId, newQty);
    } else if (action === "remove") {
      apiRemoveCartItem(cartItemId);
    }
  });

  checkoutBtn.onclick = async () => {
    const nameInput = $("#checkout-name");
    const addrInput = $("#checkout-address");
    const phoneInput = $("#checkout-phone");
    const couponInput = $("#checkout-coupon");

    const name = nameInput.value.trim();
    const addr = addrInput.value.trim();
    const phone = phoneInput.value.trim();
    const coupon = couponInput.value.trim();

    errorEl.classList.add("sp-hidden");
    successEl.classList.add("sp-hidden");

    if (
      !CartState.cart ||
      !CartState.cart.items ||
      !CartState.cart.items.length
    ) {
      errorEl.textContent = "Giỏ hàng đang trống.";
      errorEl.classList.remove("sp-hidden");
      return;
    }
    if (!name || !addr || !phone) {
      errorEl.textContent =
        "Vui lòng nhập đầy đủ tên, địa chỉ và số điện thoại.";
      errorEl.classList.remove("sp-hidden");
      return;
    }

    try {
      const selectedPaymentId = paymentSelect ? paymentSelect.value : "";

      if (!selectedPaymentId) {
        errorEl.textContent = "Vui lòng chọn phương thức thanh toán.";
        errorEl.classList.remove("sp-hidden");
        return;
      }

      const order = await apiCheckout({
        userId: AppState.currentUser.userId,
        receiverName: name,
        receiverAddress: addr,
        receiverPhone: phone,
        couponCode: coupon,
        paymentId: selectedPaymentId,
      });


      console.log("Order created:", order);
      successEl.textContent =
        "Đặt hàng thành công. Cảm ơn bạn đã mua sắm tại SellPhone!";
      successEl.classList.remove("sp-hidden");
      await apiFetchCart();
    } catch (err) {
      console.error(err);
      errorEl.textContent = `Thanh toán thất bại: ${err.message}`;
      errorEl.classList.remove("sp-hidden");
    }
  };

  apiFetchCart();
}

// ============ PAGE INIT: ACCOUNT ============
function initAccountPage() {
  const guestEl = $("#account-guest");
  const userEl = $("#account-user");

  if (!guestEl || !userEl) return;

  if (!AppState.currentUser) {
    guestEl.classList.remove("sp-hidden");
    userEl.classList.add("sp-hidden");
    updateCartHeaderCount();
    return;
  }

  guestEl.classList.add("sp-hidden");
  userEl.classList.remove("sp-hidden");

  $("#account-name").textContent = AppState.currentUser.userName || "(chưa có)";
  $("#account-email").textContent = AppState.currentUser.email || "(chưa có)";
  $("#account-phone").textContent = AppState.currentUser.phone || "(chưa có)";
  $("#account-address").textContent =
    AppState.currentUser.address || "(chưa có)";

  apiFetchCart();
}

// ============ MAIN BOOTSTRAP ============
document.addEventListener("DOMContentLoaded", () => {
  loadUser();
  updateHeaderUI();
  updateCartHeaderCount();

  const page = document.body.dataset.page;

  switch (page) {
    case "login":
      initLoginPage();
      break;
    case "register":
      initRegisterPage();
      break;
    case "products":
      initProductsPage();
      break;
    case "cart":
      initCartPage();
      break;
    case "account":
      initAccountPage();
      break;
    case "home":
    default:
      if (AppState.currentUser) {
        apiFetchCart();
      }
      break;
  }
});
