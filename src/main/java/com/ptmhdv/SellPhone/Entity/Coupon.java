package com.ptmhdv.SellPhone.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Coupon")
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "couponId")
    private Long couponId;

    @Column(name = "code", unique = true, nullable = false, length = 20)
    private String code;

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than 0")
    @Max(value = 100, message = "Percentage must be <= 100")
    @Min(value = 1, message = "Percentage must be >= 1")
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @NotNull(message = "Coupon type is required")

    @Column(nullable = false, length = 10)
    private String type;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(length = 20)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "coupon")
    @JsonIgnore
    private List<Orders> orders;

    @PrePersist
    protected void onCreate() {
        if (startDate == null) startDate = LocalDate.now();
        if (endDate == null) endDate = startDate.plusDays(10);
    }

}