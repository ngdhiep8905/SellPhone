package com.ptmhdv.sellphone.user.controller;

import com.ptmhdv.sellphone.user.entity.Users;
import com.ptmhdv.sellphone.user.service.UserService;
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
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    // ====== LIST USER (ADMIN) ======
    @GetMapping
    public Page<Users> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getUsers(pageable, search);
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

    // ====== UPDATE STATUS (ADMIN) ======
    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable String id, @RequestParam String status) {
        userService.updateStatus(id, status);
    }

    // =============================
    // AUTH RESPONSE DTO (trả gọn, không lộ password, không lặp JSON)
    // =============================
    public static class AuthResponse {
        public String userId;
        public String userName;
        public String email;
        public String phone;
        public String address;
        public String fullName;
        public String status;
        public String roleId;
        public String roleName;
    }

    private AuthResponse toAuthResponse(Users u) {
        AuthResponse r = new AuthResponse();
        r.userId = u.getUserId();
        r.userName = u.getUserName();
        r.email = u.getEmail();
        r.phone = u.getPhone();
        r.address = u.getAddress();
        r.fullName = u.getFullName();
        r.status = u.getStatus();
        if (u.getRole() != null) {
            r.roleId = u.getRole().getRoleId();
            r.roleName = u.getRole().getRoleName();
        }
        return r;
    }

    // ====== LOGIN USER (CLIENT) ======
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email,
                                       @RequestParam String password) {

        Users user = userService.getByEmail(email).orElse(null);

        if (user == null || user.getPassword() == null || !user.getPassword().equals(password)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "INVALID_CREDENTIALS"));
        }

        // Trả DTO gọn để tránh JSON nesting + đảm bảo có userId cho frontend
        return ResponseEntity.ok(toAuthResponse(user));
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

    // ====== REGISTER USER (CLIENT) ======
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (req == null
                || req.userName == null || req.userName.isBlank()
                || req.password == null || req.password.isBlank()
                || req.email == null || req.email.isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "MISSING_FIELDS"));
        }

        if (userService.getByEmail(req.email).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "EMAIL_EXISTS"));
        }

        Users user = new Users();
        user.setUserName(req.userName);
        user.setPassword(req.password);   // demo: plain-text
        user.setEmail(req.email);
        user.setFullName(req.fullName);
        user.setPhone(req.phone);
        user.setAddress(req.address);

        // status: bạn đang dùng "1" ở file hiện tại :contentReference[oaicite:4]{index=4}
        user.setStatus("1");

        Users saved = userService.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toAuthResponse(saved));
    }
}
