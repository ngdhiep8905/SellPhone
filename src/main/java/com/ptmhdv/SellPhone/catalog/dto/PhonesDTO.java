package com.ptmhdv.SellPhone.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PhonesDTO {
    private String phoneId;
    private String phoneName;
    private BigDecimal price;
    private String coverImageURL;
    private String phoneDescription;
    private String brandId;
    private String brandName; // Thêm tên brand để Frontend đỡ phải gọi thêm API brand

    // Thông số cấu hình
    private String chipset;
    private String ramSize;
    private String storageSize;
    private String screenInfo;
    private String batteryInfo;
    private String rearCamera;
    private String frontCamera;
    private String osVersion;
    private String color;

    // Quản lý kho
    private Integer stockQuantity;
    private String status;

    // Danh sách ảnh chi tiết
    private List<String> detailImages;
}