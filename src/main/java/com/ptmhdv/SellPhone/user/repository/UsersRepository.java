package com.ptmhdv.SellPhone.user.repository;

import com.ptmhdv.SellPhone.user.entity.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, String> {

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Login admin + get user by email
    Optional<Users> findByEmail(String email);

    Page<Users> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable
    );
    @Query("""
        SELECT u FROM Users u
        WHERE u.role.roleId = '02'
          AND (
            :search IS NULL OR :search = '' OR
            LOWER(u.userId) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(COALESCE(u.phone, '')) LIKE LOWER(CONCAT('%', :search, '%'))
          )
    """)
    Page<Users> findCustomers(@Param("search") String search, Pageable pageable);
}
