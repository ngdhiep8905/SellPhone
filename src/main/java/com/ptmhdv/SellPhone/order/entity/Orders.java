package com.ptmhdv.SellPhone.order.entity;

import com.ptmhdv.SellPhone.payment.entity.Payment;
import com.ptmhdv.SellPhone.user.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Orders {

    @Id
    @Column(name = "order_id", length = 13) // ví dụ: O + 12 số
    private String orderId;


    @Column(name = "book_date")
    private LocalDateTime bookDate;

    @PrePersist
    public void generateId() {
        if (orderId == null) {
            java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            orderId = "O" + java.time.LocalDateTime.now().format(f);
        }
        if (bookDate == null) bookDate = LocalDateTime.now();
    }

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
    @JoinColumn(name = "user_id", nullable = true)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersPhones> orderPhones;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "payos_order_code")
    private Long payosOrderCode;

    @Column(name = "payos_payment_link_id", length = 100)
    private String payosPaymentLinkId;

}
