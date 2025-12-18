package com.ptmhdv.SellPhone.user.controller;

import com.ptmhdv.SellPhone.user.dto.UserLoginResponse;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public UserLoginResponse login(@RequestParam String email, @RequestParam String password) {
        Users u = authService.login(email, password);

        UserLoginResponse.RoleDto roleDto = null;
        if (u.getRole() != null) {
            roleDto = new UserLoginResponse.RoleDto(u.getRole().getRoleId(), u.getRole().getRoleName());
        }

        return new UserLoginResponse(
                u.getUserId(),
                u.getEmail(),
                u.getFullName(),
                u.getPhone(),
                u.getAddress(),
                roleDto
        );
    }
}
