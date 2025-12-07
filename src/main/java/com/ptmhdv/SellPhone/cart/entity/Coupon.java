package com.ptmhdv.sellphone.cart.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.sellphone.order.entity.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@Data
public class Coupon {

    @Id
    @Column(name = "coupon_id", length = 36)
    private String couponId;

    @PrePersist
    public void generateId() {
        if (couponId == null) {
            couponId = UUID.randomUUID().toString();
        }
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = startDate.plusDays(10);
    }

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    /**
     * Giá trị giảm:
     * - Nếu type = "PERCENT" → value = 1 → 100
     * - Nếu type = "AMOUNT" → value là số tiền
     */
    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", message = "Value must be >= 0.0")
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    /**
     * Loại mã giảm giá:
     * - PERCENT (giảm theo %)
     * - AMOUNT (giảm theo số tiền)
     */
    @NotBlank(message = "Coupon type is required")
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // ACTIVE / EXPIRED / DISABLED
    @Column(length = 20)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "coupon")
    @JsonIgnore
    private List<Orders> orders;
}
