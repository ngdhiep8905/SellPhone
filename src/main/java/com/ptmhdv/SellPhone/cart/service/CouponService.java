package com.ptmhdv.sellphone.cart.service;

import com.ptmhdv.sellphone.cart.entity.Coupon;
import com.ptmhdv.sellphone.cart.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepo;

    public Coupon getByCode(String code) {
        return couponRepo.findByCode(code).orElse(null);
    }

    public Coupon save(Coupon coupon) {
        return couponRepo.save(coupon);
    }

    public boolean exists(String code) {
        return couponRepo.existsByCode(code);
    }

    public BigDecimal applyCoupon(String couponCode, BigDecimal total) {

        if (couponCode == null || couponCode.isBlank()) {
            return BigDecimal.ZERO; // không có mã → không giảm
        }

        Coupon coupon = couponRepo.findByCode(couponCode)
                .orElseThrow(() -> new RuntimeException("Coupon không tồn tại"));

        // Kiểm tra trạng thái
        if (!"ACTIVE".equalsIgnoreCase(coupon.getStatus())) {
            throw new RuntimeException("Coupon không còn hiệu lực");
        }

        // Kiểm tra hạn dùng
        LocalDate today = LocalDate.now();
        if (coupon.getStartDate() != null && today.isBefore(coupon.getStartDate())) {
            throw new RuntimeException("Coupon chưa đến thời gian sử dụng");
        }
        if (coupon.getEndDate() != null && today.isAfter(coupon.getEndDate())) {
            throw new RuntimeException("Coupon đã hết hạn");
        }

        BigDecimal discount = BigDecimal.ZERO;

        switch (coupon.getType().toUpperCase()) {

            case "PERCENT":
                // ví dụ value = 10 → giảm 10%
                BigDecimal percent = coupon.getValue().divide(BigDecimal.valueOf(100));
                discount = total.multiply(percent);
                break;

            case "AMOUNT":
                // giảm theo số tiền
                discount = coupon.getValue();
                break;

            default:
                throw new RuntimeException("Loại coupon không hợp lệ");
        }

        // Không giảm quá tổng tiền
        if (discount.compareTo(total) > 0) {
            discount = total;
        }

        return discount;
    }

}
