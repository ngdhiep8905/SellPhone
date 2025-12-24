package com.ptmhdv.SellPhone.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_images")
@Data
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "position", nullable = false)
    private Integer position;

    // 🔥 SOFT DELETE
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_id", nullable = false)
    @JsonIgnore
    private Phones phone;
}


