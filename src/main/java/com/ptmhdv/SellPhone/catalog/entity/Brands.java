package com.ptmhdv.SellPhone.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "brands")
@Data
public class Brands {

    @Id
    @Column(name = "brand_id", length = 4, nullable = false, updatable = false)
    private String brandId;

    @NotBlank(message = "Brand name is required")
    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "brand_description", columnDefinition = "TEXT")
    private String brandDescription;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Phones> phones;
}
