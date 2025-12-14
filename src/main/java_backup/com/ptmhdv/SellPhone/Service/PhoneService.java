package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Phones;
import com.ptmhdv.SellPhone.Repository.PhonesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneService {

    @Autowired
    private PhonesRepository phoneRepo;

    public List<Phones> getAll() {
        return phoneRepo.findAll();
    }

    public Phones getById(String id) {
        return phoneRepo.findById(id).orElse(null);
    }

    public Phones save(Phones phone) {
        return phoneRepo.save(phone);
    }

    public void delete(String id) {
        phoneRepo.deleteById(id);
    }

    public List<Phones> search(String keyword) {
        return phoneRepo.findByPhoneNameContainingIgnoreCase(keyword);
    }
}

