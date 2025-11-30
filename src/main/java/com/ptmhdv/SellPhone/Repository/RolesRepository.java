package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
}