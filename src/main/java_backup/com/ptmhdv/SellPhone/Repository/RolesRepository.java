package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles, String> {

    Roles findByRoleName(String roleName);
}
