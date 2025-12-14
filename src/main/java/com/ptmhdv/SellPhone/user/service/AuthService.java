package com.ptmhdv.SellPhone.user.service;

import com.ptmhdv.SellPhone.user.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserService userService; // Dùng UserService của bạn để tìm user

    public Users login(String email, String rawPassword) {

        // 1. Tìm người dùng theo Email
        Optional<Users> userOpt = userService.getByEmail(email);

        if (userOpt.isEmpty()) {
            return null; // Không tìm thấy email
        }

        Users user = userOpt.get();

        // 2. So sánh Mật khẩu (So sánh chuỗi trực tiếp)
        // **Đây là phần thay thế cho BCryptPasswordEncoder.matches()**
        if (!user.getPassword().equals(rawPassword)) {
            return null; // Sai mật khẩu
        }

        // 3. Kiểm tra Vai trò (Admin Check)
        // Giả định: Vai trò Admin có tên Role là "ADMIN"
        // (Yêu cầu Entity Roles của bạn phải có trường và getter getRoleName())
        if (user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().getRoleName())) {
            return user; // Đăng nhập thành công và là Admin
        }

        // Người dùng hợp lệ nhưng không phải Admin
        return null;
    }
}