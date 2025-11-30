package com.ptmhdv.SellPhone.Mapper;

import com.ptmhdv.SellPhone.Entity.Roles;
import com.ptmhdv.SellPhone.dto.RolesDTO;

public class RolesMapper {
    public static RolesDTO toDTO(Roles e) {
        if (e == null) return null;

        RolesDTO d = new RolesDTO();
        // Nếu entity dùng roleId thì sửa lại cho khớp
        d.setId(e.getRoleID());
        d.setRoleName(e.getRoleName());
        d.setRoleDescription(e.getRoleDescription());
        return d;
    }

    public static Roles toEntity(RolesDTO d) {
        if (d == null) return null;

        Roles e = new Roles();
        e.setRoleID(d.getId());
        e.setRoleName(d.getRoleName());
        e.setRoleDescription(d.getRoleDescription());
        return e;
    }
}
