package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Roles;

import java.util.List;

public interface RolesService {
    Roles create(Roles role);

    Roles update(Integer id, Roles role);

    void delete(Integer id);

    Roles getById(Integer id);

    List<Roles> getAll();
}
