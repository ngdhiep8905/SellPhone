package com.ptmhdv.SellPhone.order.entity;

import com.ptmhdv.SellPhone.cart.entity.Coupon;
import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.user.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @Column(name = "order_id", length = 36)
    private String orderId;

    @PrePersist
    public void generateId() {
        if (orderId == null) {
            orderId = java.util.UUID.randomUUID().toString();
        }
        if (bookDate == null) {
            bookDate = LocalDate.now();
        }
    }

    @Column(name = "book_date")
    private LocalDate bookDate;

    @Column(name = "recipient_name", length = 100)
    private String recipientName;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 12, fraction = 2)
    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "order_status", length = 20)
    private String status = "PENDING";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersPhones> orderPhones;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
