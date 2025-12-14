package com.ptmhdv.sellphone.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.sellphone.cart.entity.CartItem;
import com.ptmhdv.sellphone.order.entity.OrdersPhones;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "phones")
@Data
public class Phones {

    @Id
    @Column(name = "phone_id", length = 6)
    private String phoneId;

    @PrePersist
    public void generateId() {
        if (phoneId == null) {
            phoneId = generateCustomId();
        }
    }
    private String generateCustomId() {
        return String.format("%06d", (int)(Math.random() * 999999));
    }


    @NotBlank(message = "Phone name is required")
    @Size(min = 3, max = 100, message = "Phone name must be between 3 and 100 characters")
    @Column(name = "phone_name", unique = true, nullable = false, length = 100)
    private String phoneName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 12, fraction = 2)
    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    @Column(name = "phone_image", length = 255)
    private String coverImageURL;

    @Column(name = "phone_description", columnDefinition = "TEXT")
    private String phoneDescription;

    // LIÊN KẾT VỚI BRAND (UUID)
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brands brand;

    // TRONG ĐƠN HÀNG
    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrdersPhones> orderPhones;

    // TRONG GIỎ HÀNG
    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItems;
}
