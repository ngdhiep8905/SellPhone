package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Roles;
import com.ptmhdv.SellPhone.Repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RolesRepository rolesRepo;

    public Roles getByName(String name) {
        return rolesRepo.findByRoleName(name);
    }
}

