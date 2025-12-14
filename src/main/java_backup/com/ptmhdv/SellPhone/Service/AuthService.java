package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepo;

    public Users login(String email, String password) {
        Users user = usersRepo.findByEmail(email).orElse(null);

        if (user == null) return null;
        if (!user.getPassword().equals(password)) return null;
        if (!user.getRole().getRoleName().equals("ADMIN")) return null;

        return user;
    }
}
