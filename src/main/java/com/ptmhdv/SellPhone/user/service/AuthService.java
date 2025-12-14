package com.ptmhdv.SellPhone.user.service;

import com.ptmhdv.SellPhone.user.dto.UsersDTO;
import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.mapper.UsersMapper;
import com.ptmhdv.SellPhone.user.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepo;

    public UsersDTO login(String email, String password) {
        Users user = usersRepo.findByEmail(email).orElse(null);

        if (user == null) return null;
        if (user.getPassword() == null || !user.getPassword().equals(password)) return null;

        // tránh NullPointer nếu user chưa có role
        if (user.getRole() == null || user.getRole().getRoleName() == null) return null;

        // bạn đang giới hạn ADMIN thì giữ nguyên
        if (!user.getRole().getRoleName().equals("ADMIN")) return null;

        return UsersMapper.toDTO(user);
    }
}
