package com.ptmhdv.sellphone.user.service;

import com.ptmhdv.sellphone.user.entity.Roles;
import com.ptmhdv.sellphone.user.repository.RolesRepository;
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

