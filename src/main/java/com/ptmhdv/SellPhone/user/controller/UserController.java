package com.ptmhdv.SellPhone.user.controller;

import com.ptmhdv.SellPhone.user.entity.Roles;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.repository.RolesRepository;
import com.ptmhdv.SellPhone.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")

public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RolesRepository rolesRepository;
    //List USER
    @GetMapping
    public Page<Users> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getCustomers(pageable, search);
    }


    // ====== GET ONE USER ======
    @GetMapping("/{id}")
    public Users getUser(@PathVariable String id) {
        return userService.getUserById(id).orElse(null);
    }

    // ====== CREATE / UPDATE USER (ADMIN) ======
    @PostMapping
    public Users save(@RequestBody Users user) {
        return userService.save(user);
    }

    // ====== DELETE USER (ADMIN) ======
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }


    // ====== LOGIN USER (CLIENT) ======
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email,
                                       @RequestParam String password) {

        Users user = userService.getByEmail(email).orElse(null);

        // Sai email hoặc mật khẩu
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "INVALID_CREDENTIALS"));
        }

        // Ở DB status đang là int 1/0, nên tạm bỏ check ACTIVE
        // Nếu muốn dùng, sửa entity + logic cho đúng kiểu

        return ResponseEntity.ok(user);
    }

    // ====== REGISTER USER (CLIENT) ======
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (req == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "INVALID_REQUEST"));
        }

        String email = req.email == null ? "" : req.email.trim();
        String password = req.password == null ? "" : req.password.trim();
        String phone = req.phone == null ? "" : req.phone.trim();

        if (email.isBlank() || password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "MISSING_FIELDS",
                            "detail", "Email và mật khẩu là bắt buộc"));
        }

        String fullName = (req.fullName != null && !req.fullName.isBlank())
                ? req.fullName.trim()
                : (req.userName != null && !req.userName.isBlank() ? req.userName.trim() : "");

        if (fullName.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "FULL_NAME_REQUIRED",
                            "detail", "Vui lòng nhập họ tên"));
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "INVALID_EMAIL",
                            "detail", "Email không đúng định dạng. Ví dụ: abc@gmail.com"));
        }

        int minPasswordLen = 6;
        if (password.length() < minPasswordLen) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "WEAK_PASSWORD",
                            "detail", "Mật khẩu phải có ít nhất " + minPasswordLen + " ký tự"));
        }

        if (phone.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "PHONE_REQUIRED",
                            "detail", "Vui lòng nhập số điện thoại"));
        }
        if (!phone.matches("^\\d{10}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "INVALID_PHONE",
                            "detail", "Số điện thoại chỉ được chứa chữ số (10 chữ số)."));
        }

        if (userService.getByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "EMAIL_EXISTS",
                            "detail", "Email đã tồn tại. Vui lòng dùng email khác."));
        }

        // --- load role USER: role_id = '02' (CHAR(2)) ---
        Roles userRole = rolesRepository.findById("02")
                .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND: 02"));

        Users user = new Users();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setAddress(req.address);

        // set role entity => DB tự lưu role_id
        user.setRole(userRole);

        Users saved = userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("message", "REGISTER_SUCCESS",
                        "userId", saved.getUserId(),
                        "email", saved.getEmail(),
                        "fullName", saved.getFullName())
        );
    }



    // DTO đơn giản cho đăng ký
    public static class RegisterRequest {
        public String userName;
        public String password;
        public String email;
        public String fullName;
        public String phone;
        public String address;
    }
}
