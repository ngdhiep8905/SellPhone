package com.ptmhdv.SellPhone.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Orders")
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Long orderId;

    @Column(name = "bookDate")
    private LocalDateTime bookDate;

    @Column(name = "recipientName", length = 100)
    private String recipientName;

    @Column(name = "recipientPhone", length = 20)
    private String recipientPhone;

    @Column(name = "shippingAddress", length = 500)
    private String shippingAddress;

    @NotNull(message = "totalPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total Price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "totalPrice", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(length = 20)
    private String status = "ACTIVE";


    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "couponId")
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "paymentId")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersPhones> orderPhones;



    public void setCreatedAt(LocalDateTime now) {
    }

    public void setReceiverName(String receiverName) {
    }

    public void setReceiverAddress(String receiverAddress) {
    }

    public void setReceiverPhone(String receiverPhone) {
    }
}
