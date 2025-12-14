package com.ptmhdv.sellphone.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ptmhdv.sellphone.order.entity.Orders;
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
    @Column(name = "payment_id", length = 36)
    private String paymentId;

    @PrePersist
    public void generateId() {
        if (paymentId == null) {
            paymentId = UUID.randomUUID().toString();
        }
    }

    @NotBlank(message = "Payment method is required")
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Size(max = 50)
    @Column(name = "payment_status", length = 50)
    private String paymentStatus = "PENDING";  // default

    @OneToMany(mappedBy = "payment")
    @JsonIgnore
    private List<Orders> orders;
}
