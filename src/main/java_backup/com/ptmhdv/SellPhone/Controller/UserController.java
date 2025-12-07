package com.ptmhdv.SellPhone.Controller;

import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Page<Users> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getUsers(pageable, search);
    }

    @GetMapping("/{id}")
    public Users getUser(@PathVariable String id) {
        return userService.getUserById(id).orElse(null);
    }

    @PostMapping
    public Users save(@RequestBody Users user) {
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }

    @PutMapping("/{id}/status")
    public void updateStatus(@PathVariable String id, @RequestParam String status) {
        userService.updateStatus(id, status);
    }
}
