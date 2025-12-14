package com.ptmhdv.SellPhone.order.mapper;

import com.ptmhdv.SellPhone.order.entity.Orders;
import com.ptmhdv.SellPhone.order.entity.OrdersPhones;
import com.ptmhdv.SellPhone.order.dto.OrdersDTO;
import com.ptmhdv.SellPhone.order.dto.OrdersPhonesDTO;

import java.util.stream.Collectors;

public class OrdersMapper {

    // [PHƯƠNG THỨC ĐÃ SỬA LỖI] Ánh xạ Entity OrdersPhones sang DTO
    public static OrdersPhonesDTO toOrdersPhonesDTO(OrdersPhones e) {
        OrdersPhonesDTO d = new OrdersPhonesDTO();

        // [SỬA LỖI QUAN TRỌNG] Không còn getOrderDetailId().
        // Thay vào đó, lấy các thành phần ID từ Khóa tổng hợp (e.getId()).
        if (e.getId() != null) {
            // Giả định OrdersPhonesDTO vẫn cần một trường ID duy nhất (UUID)
            // Nếu OrdersPhonesDTO KHÔNG có trường setId(String), hãy xóa dòng này.
            // Nếu bạn muốn hiển thị Khóa tổng hợp dưới dạng chuỗi nối, bạn phải tạo logic đó
            // Tạm thời bỏ qua setId nếu DTO không có trường đó.

            // Thay vào đó, chúng ta ánh xạ các thành phần chính:
            d.setOrderId(e.getId().getOrder());
            d.setPhoneId(e.getId().getPhone());
        } else {
            // Lấy ID từ Entity lồng nếu không có Khóa tổng hợp (Dành cho trường hợp ID của DTO là ID Entity lồng)
            d.setOrderId(e.getOrder().getOrderId());
            d.setPhoneId(e.getPhone().getPhoneId());
        }

        // Lưu ý: dòng này bị lặp lại, chỉ nên dùng 1 trong 2:
        // d.setPrice(e.getPrice());
        // d.setPrice(e.getTotalPrice());

        // Giữ lại các trường dữ liệu còn lại
        d.setQuantity(e.getQuantity());
        d.setPrice(e.getPrice()); // Đảm bảo lấy giá đơn vị
        // Nếu DTO có trường totalPrice riêng:
        // d.setTotalPrice(e.getTotalPrice());

        return d;
    }

    public static OrdersDTO toDTO(Orders e) {
        OrdersDTO d = new OrdersDTO();
        d.setId(e.getOrderId());
        d.setUserId(e.getUser().getUserId());
        d.setStatus(e.getStatus());
        if (e.getPayment() != null)
            d.setPaymentId(e.getPayment().getPaymentId());

        d.setOrderItems(
                e.getOrderPhones().stream()
                        .map(OrdersMapper::toOrdersPhonesDTO) // Dùng phương thức đã sửa
                        .collect(Collectors.toList())
        );

        return d;
    }
}