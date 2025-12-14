package com.ptmhdv.SellPhone.user.controller;

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
    public Users login(@RequestParam String email, @RequestParam String password) {
        return authService.login(email, password);
    }
}
