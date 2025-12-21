package com.ptmhdv.SellPhone.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "product_images")
@Data
public class ProductImage {
    @Id
    @Column(name = "image_id")
    private int imageId;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "phone_id")
    @JsonIgnore
    private Phones phone;
}