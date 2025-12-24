package com.ptmhdv.SellPhone.user.service;

import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.repository.RolesRepository;
import com.ptmhdv.SellPhone.user.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepo;


    public Page<Users> getUsers(Pageable pageable, String search) {
        if (search == null || search.isEmpty()) {
            return usersRepo.findAll(pageable);
        }
        return usersRepo.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
    }

    public Optional<Users> getUserById(String id) {
        return usersRepo.findById(id);
    }
    private String generateUserId() {
        // Sinh id dạng 000001 .. 999999 giống data mẫu của bạn
        for (int i = 0; i < 50; i++) {
            int n = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 1_000_000);
            String id = String.format("%06d", n);

            if (!usersRepo.existsById(id)) {
                return id;
            }
        }

        // fallback rất hiếm
        long t = System.currentTimeMillis() % 1_000_000;
        return String.format("%06d", t);
    }
    public Users save(Users user) {

        // Nếu userId chưa có thì tự sinh (DB: users.user_id CHAR(6))
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            user.setUserId(generateUserId());
        }

        return usersRepo.save(user);
    }

    public void delete(String id) {
        usersRepo.deleteById(id);
    }



    public Optional<Users> getByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

    public Page<Users> getCustomers(Pageable pageable, String search) {
        return usersRepo.findCustomers(search, pageable);
    }


}
