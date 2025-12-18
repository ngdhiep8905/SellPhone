package com.ptmhdv.SellPhone.user.dto;

public record UserLoginResponse(
        String userId,
        String email,
        String fullName,
        String phone,
        String address,
        RoleDto role
) {
    public record RoleDto(String roleId, String roleName) {}
}
