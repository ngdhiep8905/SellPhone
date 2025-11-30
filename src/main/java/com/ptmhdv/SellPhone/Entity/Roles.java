package com.ptmhdv.SellPhone.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name="Roles")
@Data
public class Roles {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="RoleID")
    private Long roleID;

    @NotBlank(message = "RoleName is required")
    @Column(name = "RoleName", nullable = false, length = 50)
    private String roleName;

    @Column(name="RoleDescription", columnDefinition = "TEXT")
    private String roleDescription;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<Users> users;

}

