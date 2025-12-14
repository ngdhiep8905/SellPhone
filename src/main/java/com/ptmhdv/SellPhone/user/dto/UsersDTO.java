package com.ptmhdv.sellphone.user.dto;

import lombok.Data;

@Data
public class UsersDTO {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String roleId;
}
