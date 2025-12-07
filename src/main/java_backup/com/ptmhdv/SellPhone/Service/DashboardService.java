package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Orders;
import com.ptmhdv.SellPhone.Repository.OrdersPhonesRepository;
import com.ptmhdv.SellPhone.Repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private OrdersRepository ordersRepo;

    @Autowired
    private OrdersPhonesRepository orderDetailRepo;

    // LV1
    public Double getRevenueOfMonth(int month) {
        return ordersRepo.sumRevenueOfMonth(month);
    }

    // LV2 combo chart
    public List<Orders> filterOrders(Integer month, Integer year) {
        return ordersRepo.filterOrders(month, year);
    }

    public List<Object[]> getTopProducts() {
        return orderDetailRepo.getTopProducts();
    }

    public List<Object[]> getRevenueByBrand() {
        return orderDetailRepo.getRevenueByBrand();
    }
}
