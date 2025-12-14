package com.ptmhdv.SellPhone.user.mapper;

import com.ptmhdv.SellPhone.user.entity.Roles;
import com.ptmhdv.SellPhone.user.dto.RolesDTO;

public class RolesMapper {
    public static RolesDTO toDTO(Roles e) {
        if (e == null) return null;

        RolesDTO d = new RolesDTO();
        // Nếu entity dùng roleId thì sửa lại cho khớp
        d.setId(e.getRoleId());
        d.setRoleName(e.getRoleName());
        d.setRoleDescription(e.getRoleDescription());
        return d;
    }

    public static Roles toEntity(RolesDTO d) {
        if (d == null) return null;

        Roles e = new Roles();
        e.setRoleId(d.getId());
        e.setRoleName(d.getRoleName());
        e.setRoleDescription(d.getRoleDescription());
        return e;
    }
}
