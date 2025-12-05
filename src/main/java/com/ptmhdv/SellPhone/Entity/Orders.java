package com.ptmhdv.SellPhone.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
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

    // Thông tin người nhận
    @Column(name = "recipient_name", length = 100)
    private String recipientName;

    @Column(name = "recipient_phone", length = 20)
    private String recipientPhone;

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    // Tổng tiền của đơn
    @NotNull(message = "totalPrice is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total Price must be greater than 0")
    @Digits(integer = 12, fraction = 2)
    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalPrice;

    // Order Status: PROCESSING / COMPLETED / CANCELLED / PENDING
    @Column(name = "order_status", length = 20)
    private String status = "PENDING";

    // User đặt đơn
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    // Phương thức thanh toán
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // Chi tiết đơn hàng
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersPhones> orderPhones;

}
