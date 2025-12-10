package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.entity.Phones;

import java.util.List;

public interface PhoneService {
    List<Phones> getAllPhones();

    List<Phones> getAllPhonesSorted(String sortBy, String sortOrder);

    Phones getPhoneById(String id);

    List<Phones> searchPhones(String keyword);

    Phones createPhone(PhonesDTO dto);

    Phones updatePhone(String PhoneId, PhonesDTO dto);

    void deletePhone(String PhoneId);
}
