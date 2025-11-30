package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Users;
import com.ptmhdv.SellPhone.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Override
    public Users register(Users user) {
        if (usersRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Không mã hoá mật khẩu
        user.setPassword(user.getPassword());

        return usersRepository.save(user);
    }

    @Override
    public Users login(String email, String password) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // So sánh mật khẩu thô
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    @Override
    public Users update(Long userId, Users data) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (data.getUserName() != null) user.setUserName(data.getUserName());
        if (data.getPhone() != null) user.setPhone(data.getPhone());
        if (data.getAddress() != null) user.setAddress(data.getAddress());

        if (data.getEmail() != null && !data.getEmail().equals(user.getEmail())) {
            if (usersRepository.existsByEmail(data.getEmail()))
                throw new RuntimeException("Email already exists");
            user.setEmail(data.getEmail());
        }

        // Không mã hoá password
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            user.setPassword(data.getPassword());
        }

        return usersRepository.save(user);
    }

    @Override
    public Users getById(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<Users> getAll() {
        return usersRepository.findAll();
    }
}
