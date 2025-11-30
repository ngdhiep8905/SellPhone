package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Coupon;

import java.util.List;

public interface CouponService {
    Coupon create(Coupon coupon);

    Coupon update(Integer id, Coupon coupon);

    void delete(Integer id);

    Coupon getById(Integer id);

    Coupon findValidCoupon(String code);

    List<Coupon> getAll();
}
