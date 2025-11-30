package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Orders;

public interface OrdersService {
    Orders checkout(Long userId,
                    String receiverName,
                    String receiverAddress,
                    String receiverPhone,
                    String couponCode,
                    Integer paymentId);
}
