package com.ptmhdv.SellPhone.user.service;

import com.ptmhdv.SellPhone.user.entity.Roles;
import com.ptmhdv.SellPhone.user.repository.RolesRepository;
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

