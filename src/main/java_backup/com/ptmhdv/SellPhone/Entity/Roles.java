package com.ptmhdv.SellPhone.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
public class Roles {

    @Id
    @Column(name = "role_id", length = 36)
    private String roleId;

    @PrePersist
    public void generateId() {
        if (roleId == null) {
            roleId = UUID.randomUUID().toString();
        }
    }

    @NotBlank(message = "Role name is required")
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "role_description", columnDefinition = "TEXT")
    private String roleDescription;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private List<Users> users;
}
