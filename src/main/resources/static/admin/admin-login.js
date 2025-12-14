// Dùng cùng host/port với Spring Boot (server.port=8086)
const API_BASE_URL = "";

function login() {
  // Lấy các element trong HTML
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");
  const errorMsg = document.getElementById("errorMsg");

  const email = emailInput.value.trim();
  const pass = passwordInput.value.trim();

  if (!email || !pass) {
    errorMsg.innerText = "Vui lòng nhập email và mật khẩu.";
    return;
  }

  // Gửi dạng query param đúng với @RequestParam ở AuthController
  const params = new URLSearchParams({ email, password: pass });

  fetch(`${API_BASE_URL}/api/auth/login?${params.toString()}`, {
    method: "POST",
  })
    .then((res) => {
      if (!res.ok) {
        // Sai tài khoản / lỗi server → Spring trả 4xx/5xx
        throw new Error("HTTP " + res.status);
      }
      return res.json();
    })
    .then((user) => {
      // Nếu backend trả null thì user sẽ là null
      if (!user) {
        errorMsg.innerText = "Sai tài khoản hoặc mật khẩu!";
        return;
      }

      // Đánh dấu đã đăng nhập admin
      localStorage.setItem("isAdmin", "true");

      // Chuyển sang trang admin
      window.location.href = "admin.html";
    })
    .catch((err) => {
      console.error(err);
      errorMsg.innerText = "Không kết nối được tới server.";
    });
}

function logout() {
  localStorage.removeItem("isAdmin");
  window.location.href = "admin-login.html";
}
