package com.ptmhdv.sellphone.dashboard.service;

import com.ptmhdv.sellphone.catalog.repository.PhonesRepository;
import com.ptmhdv.sellphone.dashboard.DTO.DashboardDTO;
import com.ptmhdv.sellphone.order.entity.Orders;
import com.ptmhdv.sellphone.order.repository.OrdersPhonesRepository;
import com.ptmhdv.sellphone.order.repository.OrdersRepository;
import com.ptmhdv.sellphone.user.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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



    // LV2 combo chart
    public List<Orders> filterRevenue(Integer month, Integer year) {
        return ordersRepo.filterOrders(month, year);
    }
    public List<Object[]> getRevenueMonthly(){
        return ordersRepo.getRevenueMonthly();
    }

    public List<Object[]> getTopProducts() {
        return orderDetailRepo.getTopProducts(PageRequest.of(0, 5));
    }


    public List<Object[]> getRevenueByBrand() {
        return orderDetailRepo.getRevenueByBrand();
    }

    public DashboardDTO getSummary() {

        long totalProducts = phoneRepo.count();
        long totalOrders = ordersRepo.sumOrdersOfMonth();
        long totalUsers = userRepo.count();

        BigDecimal revenue = ordersRepo.getMonthRevenue();
        long monthRevenue = revenue == null ? 0 : revenue.longValue();

        return new DashboardDTO(totalProducts, totalOrders, totalUsers, monthRevenue);
    }


}
