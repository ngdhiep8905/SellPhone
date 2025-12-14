package com.ptmhdv.SellPhone.user.mapper;

import com.ptmhdv.SellPhone.user.entity.Users;
import com.ptmhdv.SellPhone.user.dto.UsersDTO;

public class UsersMapper {
    public static UsersDTO toDTO(Users e) {
        UsersDTO d = new UsersDTO();
        d.setId(e.getUserId());
        d.setUsername(e.getFullName());
        d.setEmail(e.getEmail());
        d.setPhone(e.getPhone());
        d.setAddress(e.getAddress());
        if (e.getRole() != null)
            d.setRoleId(e.getRole().getRoleId());
        return d;
    }
}
