package com.ptmhdv.SellPhone.order.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

// Đánh dấu là lớp có thể nhúng
@Embeddable
@Data
@EqualsAndHashCode
public class OrdersPhonesId implements Serializable {

    // Phải match kiểu dữ liệu và tên cột trong @JoinColumn của lớp cha
    private String order; // Ánh xạ đến order_id
    private String phone; // Ánh xạ đến phone_id

    // Hibernate yêu cầu constructor không tham số
    public OrdersPhonesId() {}

    public OrdersPhonesId(String orderId, String phoneId) {
        this.order = orderId;
        this.phone = phoneId;
    }
}