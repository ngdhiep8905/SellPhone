package com.ptmhdv.sellphone.order.repository;

import com.ptmhdv.sellphone.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
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


    @Query("""
    SELECT COALESCE(SUM(o.totalPrice), 0)
    FROM Orders o
    WHERE MONTH(o.bookDate) = MONTH(CURRENT_DATE)
      AND YEAR(o.bookDate) = YEAR(CURRENT_DATE)
""")
    BigDecimal getMonthRevenue();


}
