package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Brands;
import org.springframework.stereotype.Service;
import java.util.List;
public interface BrandsService {
    List<Brands> getAll();

    Brands getById(Long id);

    Brands create(Brands brand);

    Brands update(Long id, Brands brand);

    void delete(Long id);
}
