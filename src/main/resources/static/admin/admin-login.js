const API_BASE_URL = "";
function login() {
    const email = emailInput.value.trim();
    const pass = password.value.trim();

    fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ email: email, password: pass })
    })
    .then(res => res.json())
    .then(data => {
        if (data.status !== "OK") {
            errorMsg.innerText = "Sai tài khoản hoặc mật khẩu!";
            return;
        }

        // ĐÁNH DẤU ĐÃ ĐĂNG NHẬP
        localStorage.setItem("isAdmin", "true");

        // chuyển sang trang admin
        window.location.href = "admin.html";
    });
}
function logout() {
    localStorage.removeItem("isAdmin");
    window.location.href = "admin-login.html";
}
