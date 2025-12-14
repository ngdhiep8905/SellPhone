package com.ptmhdv.SellPhone.user.service;

import com.ptmhdv.SellPhone.user.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    public Users login(String email, String rawPassword) {

        Optional<Users> userOpt = userService.getByEmail(email);

        if (userOpt.isEmpty()) {
            // Trả về lỗi 401 nếu không tìm thấy email
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu.");
        }

        Users user = userOpt.get();

        // 1. So sánh Mật khẩu (So sánh chuỗi trực tiếp)
        if (!user.getPassword().equals(rawPassword)) {
            // Trả về lỗi 401 nếu mật khẩu không khớp
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu.");
        }

        // [QUAN TRỌNG]: Nếu mật khẩu khớp, trả về User, bất kể vai trò là gì.
        // Điều này cho phép KHÁCH HÀNG (role='USER') đăng nhập.
        return user;
    }
}