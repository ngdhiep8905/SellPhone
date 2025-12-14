package com.ptmhdv.sellphone.cart.mapper;

import com.ptmhdv.sellphone.cart.entity.Coupon;
import com.ptmhdv.sellphone.cart.dto.CouponDTO;

public class CouponMapper {
    public static CouponDTO toDTO(Coupon e) {
        CouponDTO d = new CouponDTO();
        d.setId(e.getCouponId());
        d.setCode(e.getCode());
        d.setValue(e.getValue());
        d.setStatus(e.getStatus());
        return d;
    }

    public static Coupon toEntity(CouponDTO d) {
        Coupon e = new Coupon();
        e.setCouponId(d.getId());
        e.setCode(d.getCode());
        e.setValue(d.getValue());
        e.setStatus(d.getStatus());
        return e;
    }
}
