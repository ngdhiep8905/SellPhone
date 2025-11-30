package com.ptmhdv.SellPhone.dto;

import lombok.Data;

@Data
public class UsersDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private Long roleId;
}
