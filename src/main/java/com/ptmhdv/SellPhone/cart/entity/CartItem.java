package com.ptmhdv.SellPhone.cart.entity;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@Setter
@Getter
@Entity
@Table(name = "cart_item")
@Data
public class CartItem {

    @Id
    @Column(name = "cart_item_id", length = 36)
    private String cartItemId;

    @PrePersist
    public void generateId() {
        if (cartItemId == null) {
            cartItemId = UUID.randomUUID().toString();
        }
    }

    // Mỗi Cart có nhiều CartItem
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // Mỗi CartItem gắn với một Phone
    @ManyToOne
    @JoinColumn(name = "phone_id")
    private Phones phone;

    // Số lượng sản phẩm
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    public BigDecimal getQuantityPrice() {
        if (phone == null || phone.getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return phone.getPrice().multiply(BigDecimal.valueOf(quantity));
    }


}
