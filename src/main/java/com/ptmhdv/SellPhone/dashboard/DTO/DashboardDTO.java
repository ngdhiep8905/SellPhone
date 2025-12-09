package com.ptmhdv.sellphone.dashboard.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DashboardDTO {
    private long totalProducts;
    private long totalOrders;
    private long totalUsers;
    private long monthRevenue;

    public DashboardDTO(long totalProducts, long totalOrders, long totalUsers, long monthRevenue) {
        this.totalProducts = totalProducts;
        this.totalOrders = totalOrders;
        this.totalUsers = totalUsers;
        this.monthRevenue = monthRevenue;
    }


}
