package com.ptmhdv.SellPhone.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.SellPhone.order.entity.Orders;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Data
public class Payment {

    @Id
    @Column(name = "payment_id", length = 2)
    private String paymentId; // '01' hoặc '02'

    // BỎ @PrePersist generate UUID

    @NotBlank(message = "Payment method is required")
    @Size(max = 50)
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // COD / BANKING

    @Size(max = 50)
    @Column(name = "payment_status", length = 50)
    private String paymentStatus = "PENDING"; // PENDING / PAID

    @OneToMany(mappedBy = "payment")
    @JsonIgnore
    private List<Orders> orders;
}
