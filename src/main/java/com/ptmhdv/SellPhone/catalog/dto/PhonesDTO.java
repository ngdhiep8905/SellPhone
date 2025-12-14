package com.ptmhdv.SellPhone.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PhonesDTO {
    private String id;
    private String phoneName;
    private BigDecimal price;
    private String coverImageURL;
    private String phoneDescription;
    private String brandId;
}
