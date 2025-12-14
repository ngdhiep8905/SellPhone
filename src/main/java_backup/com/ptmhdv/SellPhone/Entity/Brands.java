package com.ptmhdv.SellPhone.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "brands")
@Data
public class Brands {

    @Id
    @Column(name = "brand_id", length = 36)
    private String brandId;

    @PrePersist
    public void generateId() {
        if (brandId == null) {
            brandId = UUID.randomUUID().toString();
        }
    }

    @NotBlank(message = "Brand name is required")
    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "brand_description", columnDefinition = "TEXT")
    private String brandDescription;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Phones> phones;
}
