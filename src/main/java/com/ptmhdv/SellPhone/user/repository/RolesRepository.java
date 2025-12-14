package com.ptmhdv.SellPhone.user.repository;

import com.ptmhdv.SellPhone.user.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, String> {

    Roles findByRoleName(String roleName);
}
