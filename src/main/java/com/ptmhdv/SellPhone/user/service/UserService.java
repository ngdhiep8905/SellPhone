package com.ptmhdv.sellphone.user.service;

import com.ptmhdv.sellphone.user.entity.Users;
import com.ptmhdv.sellphone.user.repository.UsersRepository;
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

    public Users save(Users user) {
        return usersRepo.save(user);
    }

    public void delete(String id) {
        usersRepo.deleteById(id);
    }

    public void updateStatus(String id, String status) {
        Users user = usersRepo.findById(id).orElseThrow();
        user.setStatus(status);
        usersRepo.save(user);
    }

      public Optional<Users> getByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

}
