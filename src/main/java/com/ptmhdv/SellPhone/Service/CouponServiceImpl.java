package com.ptmhdv.SellPhone.Service;


import com.ptmhdv.SellPhone.Entity.Coupon;
import com.ptmhdv.SellPhone.Repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public Coupon create(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon update(Integer id, Coupon updated) {
        Coupon exist = getById(id);

        exist.setCode(updated.getCode());
        exist.setValue(updated.getValue());
        exist.setType(updated.getType());
        exist.setStartDate(updated.getStartDate());
        exist.setEndDate(updated.getEndDate());
        exist.setStatus(updated.getStatus());

        return couponRepository.save(exist);
    }

    @Override
    public void delete(Integer id) {
        couponRepository.deleteById(id);
    }

    @Override
    public Coupon getById(Integer id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
    }

    @Override
    public Coupon findValidCoupon(String code) {
        return couponRepository.findByCodeAndStatus(code, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Invalid or expired coupon"));
    }

    @Override
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }
}
