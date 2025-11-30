package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Users;

import java.util.List;

public interface UsersService {
    Users register(Users user);

    Users login(String email, String password);

    Users update(Long userId, Users newInfo);

    Users getById(Long id);

    List<Users> getAll();
}
