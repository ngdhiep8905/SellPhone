package com.ptmhdv.sellphone.dashboard.service;

import com.ptmhdv.sellphone.catalog.repository.PhonesRepository;
import com.ptmhdv.sellphone.dashboard.DTO.DashboardDTO;
import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.sellphone.order.repository.OrdersPhonesRepository;
import com.ptmhdv.sellphone.order.repository.OrdersRepository;
import com.ptmhdv.sellphone.user.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private UsersRepository userRepo;

    @Autowired
    private PhonesRepository phoneRepo;

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

    public DashboardDTO getSummary(){
        long totalProducts = phoneRepo.count();
        long totalOrders = ordersRepo.count();
        long totalUsers = userRepo.count();

        Long revenue = ordersRepo.getMonthRevenue();
        long monthRevenue = (revenue != null) ? revenue : 0;
        return new DashboardDTO(totalProducts,totalOrders,totalUsers,monthRevenue);
    }
}
