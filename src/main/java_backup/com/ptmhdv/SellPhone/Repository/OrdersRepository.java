package com.ptmhdv.SellPhone.Repository;

import com.ptmhdv.SellPhone.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String> {

    // Sum revenue by month (Dashboard LV1)
    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE MONTH(o.bookDate) = ?1")
    Double sumRevenueOfMonth(int month);

    // Dashboard advanced
    @Query("""
        SELECT o FROM Orders o
        WHERE (:month IS NULL OR MONTH(o.bookDate) = :month)
        AND (:year IS NULL OR YEAR(o.bookDate) = :year)
    """)
    List<Orders> filterOrders(Integer month, Integer year);
}
