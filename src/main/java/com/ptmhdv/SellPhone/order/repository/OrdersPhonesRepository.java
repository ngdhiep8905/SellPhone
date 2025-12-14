package com.ptmhdv.SellPhone.order.repository;

import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersPhonesRepository extends JpaRepository<OrdersPhones, String> {

    // Dashboard: top 5 sản phẩm bán chạy
    @Query("""
        SELECT op.phone.phoneName, SUM(op.quantity) 
        FROM OrdersPhones op 
        GROUP BY op.phone.phoneId 
        ORDER BY SUM(op.quantity) DESC
    """)
    List<Object[]> getTopProducts();

    // Dashboard: doanh thu theo thương hiệu
    @Query("""
    SELECT op.phone.brand.brandName, SUM(op.quantity * op.price)
    FROM OrdersPhones op
    GROUP BY op.phone.brand.brandId, op.phone.brand.brandName
    ORDER BY SUM(op.quantity * op.price) DESC
""")
    List<Object[]> getRevenueByBrand();

    @Query("""
    SELECT p.phoneName, SUM(op.quantity)
    FROM OrdersPhones op
    JOIN op.phone p
    GROUP BY p.phoneId
    ORDER BY SUM(op.quantity) DESC
    """)
    List<Object[]> getTopProducts(org.springframework.data.domain.Pageable pageable);

}
