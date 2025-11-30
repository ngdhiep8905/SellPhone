package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Orders;
import com.ptmhdv.SellPhone.Entity.OrdersPhones;
import com.ptmhdv.SellPhone.Repository.OrdersPhonesRepository;
import com.ptmhdv.SellPhone.Repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashBoardServiceImpl implements DashboardService{
    private final OrdersRepository ordersRepository;
    private final OrdersPhonesRepository opRepository;

    @Override
    public BigDecimal getTotalRevenue() {
        return ordersRepository.findAll().stream()
                .filter(o -> "COMPLETED".equalsIgnoreCase(o.getStatus()))
                .map(Orders::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<Map<String, Object>> getTopSelling(int limit) {

        Map<Long, Long> count = new HashMap<>();

        for (OrdersPhones op : opRepository.findAll()) {
            if (!"COMPLETED".equalsIgnoreCase(op.getOrder().getStatus())) continue;

            count.merge(
                    op.getPhone().getPhoneId(),
                    (long) op.getQuantity(),
                    Long::sum
            );
        }

        return count.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(e -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("phoneId", e.getKey());
                    row.put("totalSold", e.getValue());
                    return row;
                })
                .toList();
    }
}
