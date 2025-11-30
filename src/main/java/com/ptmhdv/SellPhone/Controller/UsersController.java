package com.ptmhdv.SellPhone.Controller;

import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Service.UsersService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/login")
    public LoginResponse login(@RequestParam String email,
                               @RequestParam String password) {
        Users user = usersService.login(email, password);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // map sang response FE dùng
        LoginResponse resp = new LoginResponse();
        resp.setUserId(user.getUserId());
        resp.setUserName(user.getUserName());
        resp.setEmail(user.getEmail());
        resp.setPhone(user.getPhone());
        resp.setAddress(user.getAddress());
        return resp;
    }

    @Data
    public static class LoginResponse {
        private Long userId;
        private String userName;
        private String email;
        private String phone;
        private String address;
    }
}
