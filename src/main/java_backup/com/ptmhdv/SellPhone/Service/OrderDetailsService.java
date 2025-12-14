package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Repository.OrdersPhonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailsService {

    @Autowired
    private OrdersPhonesRepository orderDetailRepo;

    public List<Object[]> getTopProducts() {
        return orderDetailRepo.getTopProducts();
    }

    public List<Object[]> getRevenueByBrand() {
        return orderDetailRepo.getRevenueByBrand();
    }
}

