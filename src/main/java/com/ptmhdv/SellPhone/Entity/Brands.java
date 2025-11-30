package com.ptmhdv.SellPhone.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Entity
@Table(name="Brands")
@Data
public class Brands {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="brandId")
    private Long brandId;

    @NotBlank(message = "brandName is required")
    @Column(name = "brandName", nullable = false, length = 50)
    private String brandName;

    @Column(name="brandDescription", columnDefinition = "TEXT")
    private String brandDescription;

    @OneToMany(mappedBy = "brand")
    @JsonIgnore
    private List<Phones> phones;


}
