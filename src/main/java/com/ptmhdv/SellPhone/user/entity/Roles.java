package com.ptmhdv.SellPhone.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Roles {

    @Id
    @Column(name = "role_id", length = 2)
    private String roleId; // ví dụ: "01" (ADMIN), "02" (USER)

    // BỎ @PrePersist generate UUID vì role_id cố định theo DB

    @NotBlank(message = "Role name is required")
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "role_description", columnDefinition = "TEXT")
    private String roleDescription;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<Users> users;
}
