package com.ptmhdv.sellphone.user.repository;

import com.ptmhdv.sellphone.user.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, String> {

    Roles findByRoleName(String roleName);
}
