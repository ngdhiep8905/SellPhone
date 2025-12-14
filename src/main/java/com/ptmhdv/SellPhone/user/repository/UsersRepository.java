package com.ptmhdv.sellphone.user.repository;

import com.ptmhdv.sellphone.user.entity.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Login admin + get user by email
    Optional<Users> findByEmail(String email);


    Optional<Users> findByUserName(String userName);

    Page<Users> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable
    );

}
