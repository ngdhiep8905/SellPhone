package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByUser_UserId(Long userId);

}
