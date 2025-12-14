package com.ptmhdv.SellPhone.order.repository;

import com.ptmhdv.SellPhone.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, String> {

    @Query("SELECT COUNT(o) FROM Orders o " +
            "WHERE FUNCTION('YEAR', o.bookDate) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', o.bookDate) = FUNCTION('MONTH', CURRENT_DATE)")
    Long sumOrdersOfMonth();

    @Query("""
        SELECT o FROM Orders o
        WHERE (:month IS NULL OR MONTH(o.bookDate) = :month)
        AND (:year IS NULL OR YEAR(o.bookDate) = :year)
    """)
    List<Orders> filterOrders(Integer month, Integer year);//can chu y

    @Query("""
        SELECT COALESCE(SUM(o.totalPrice), 0)
        FROM Orders o
        WHERE MONTH(o.bookDate) = MONTH(CURRENT_DATE)
          AND YEAR(o.bookDate) = YEAR(CURRENT_DATE)
    """)
    BigDecimal getCurrentMonthRevenue();

    @Query("""
        SELECT MONTH(o.bookDate), SUM(o.totalPrice)
        FROM Orders o
        GROUP BY MONTH(o.bookDate)
        ORDER BY MONTH(o.bookDate)
    """)
    List<Object[]> getRevenueMonthly();

    @Query("""
    SELECT o.bookDate, SUM(o.totalPrice)
    FROM Orders o
    WHERE (:month IS NULL OR MONTH(o.bookDate) = :month)
      AND (:year IS NULL OR YEAR(o.bookDate) = :year)
      AND (:brandId IS NULL OR EXISTS (
            SELECT 1 FROM OrdersPhones op
            WHERE op.order = o AND op.phone.brand.brandId = :brandId
      ))
    GROUP BY o.bookDate
    ORDER BY o.bookDate
""")
    List<Object[]> filterRevenue(Integer month, Integer year, String brandId);


}
