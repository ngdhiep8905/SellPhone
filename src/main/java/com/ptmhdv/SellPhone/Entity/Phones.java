package com.ptmhdv.SellPhone.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="Phones")
@Data
public class Phones {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="PhoneId")
    private Long phoneId;

    @NotBlank(message = "PhoneName is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Column(name = "PhoneName", unique = true, nullable = false, length = 100)
    private String phoneName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 integer digits and 2 decimal places")
    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Size(max = 255, message = "Cover image URL must not exceed 255 characters")
    @Column(name = "coverImageURL", length = 255)
    private String coverImageURL;

    @Column(name="PhoneDescription", columnDefinition = "TEXT")
    private String phoneDescription;

    @ManyToOne
    @JoinColumn(name = "brandId")
    private Brands brand;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrdersPhones> orderPhones;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItems;


}
