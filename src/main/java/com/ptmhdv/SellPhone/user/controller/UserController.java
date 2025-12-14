package com.ptmhdv.SellPhone.user.controller;

import com.ptmhdv.SellPhone.user.entity.Users;
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

        // validate đơn giản
        if (req == null
                || req.userName == null || req.userName.isBlank()
                || req.password == null || req.password.isBlank()
                || req.email == null || req.email.isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "MISSING_FIELDS"));
        }

        // kiểm tra email đã tồn tại chưa
        if (userService.getByEmail(req.email).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "EMAIL_EXISTS"));
        }

        // tạo user mới
        Users user = new Users();
        user.setFullName(req.userName);
        user.setPassword(req.password);   // hiện tại password để plain-text
        user.setEmail(req.email);
        user.setFullName(req.fullName);
        user.setPhone(req.phone);
        user.setAddress(req.address);
        // userId: generate trong @PrePersist của entity
        // status: default ACTIVE
        // role: để null → user thường

        Users saved = userService.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
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
