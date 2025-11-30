package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Phones;
import com.ptmhdv.SellPhone.dto.PhonesDTO;

import java.util.List;

public interface PhonesService {
    List<PhonesDTO> search(Long brandId, String keyword);

    // UC07
    PhonesDTO getById(Long id);

    // UC10
    PhonesDTO create(PhonesDTO dto);

    // UC11
    PhonesDTO update(Long id, PhonesDTO dto);

    // UC12
    void delete(Long id);
}
