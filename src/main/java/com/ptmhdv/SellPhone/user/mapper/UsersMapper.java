package com.ptmhdv.sellphone.user.mapper;

import com.ptmhdv.sellphone.user.entity.Users;
import com.ptmhdv.sellphone.user.dto.UsersDTO;

public class UsersMapper {
    public static UsersDTO toDTO(Users e) {
        UsersDTO d = new UsersDTO();
        d.setId(e.getUserId());
        d.setUsername(e.getUserName());
        d.setEmail(e.getEmail());
        d.setPhone(e.getPhone());
        d.setAddress(e.getAddress());
        if (e.getRole() != null)
            d.setRoleId(e.getRole().getRoleId());
        return d;
    }
}
