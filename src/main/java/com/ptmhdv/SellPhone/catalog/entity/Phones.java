package com.ptmhdv.SellPhone.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.SellPhone.cart.entity.CartItem;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Table(name = "phones")
@Data
public class Phones {

    @Id
    @Column(name = "phone_id", length = 6)
    private String phoneId;

    @NotBlank(message = "Phone name is required")
    @Column(name = "phone_name", nullable = false, length = 100)
    private String phoneName;

    // --- THÔNG SỐ KỸ THUẬT ---
    @Column(name = "chipset")
    private String chipset;

    @Column(name = "ram_size")
    private String ramSize;

    @Column(name = "storage_size")
    private String storageSize;

    @Column(name = "screen_info")
    private String screenInfo;

    @Column(name = "battery_info")
    private String batteryInfo;

    @Column(name = "rear_camera")
    private String rearCamera;

    @Column(name = "front_camera")
    private String frontCamera;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "color")
    private String color;

    // --- QUẢN LÝ KHO & GIÁ ---
    @NotNull(message = "Price is required")
    @Column(name = "price", precision = 12, scale = 2, columnDefinition = "DECIMAL(12,2)")
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "phone_image_thumb")
    private String coverImageURL;

    @Column(name = "phone_description", columnDefinition = "TEXT")
    private String phoneDescription;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brands brand;

    // --- LIÊN KẾT ẢNH CHI TIẾT ---
    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;


    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrdersPhones> orderPhones;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItems;


}
