package com.ptmhdv.SellPhone.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    BigDecimal getTotalRevenue();

    List<Map<String, Object>> getTopSelling(int limit);
}
