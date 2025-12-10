package com.ptmhdv.SellPhone.catalog.service;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.repository.PhonesRepository;
import com.ptmhdv.sellphone.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Service
@Transactional
public class PhoneServiceImpl implements PhoneService{

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PhoneServiceImpl.class);
    private PhonesRepository phonesRepository;

    public PhoneServiceImpl(PhonesRepository phonesRepository) {
        this.phonesRepository = phonesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phones> getAllPhones() {
        log.debug("Fetching all books");
        return phonesRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phones> getAllPhonesSorted(String sortBy, String sortOrder) {
        log.debug("Fetching all books sorted by: {} {}", sortBy, sortOrder);
        List<Phones> phones = phonesRepository.findAll();
        return sortPhones(phones, sortBy, sortOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Phones getPhoneById(String id) {
        log.debug("Fetching book by ID: {}", id);
        return phonesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phones> searchPhones(String keyword) {
        log.debug("Searching books with keyword: {}", keyword);
        return phonesRepository.findByPhoneNameContainingIgnoreCaseOrBrandContainingIgnoreCase(keyword, keyword);
    }

    private List<Phones> sortPhones(List<Phones> phones, String sortBy, String sortOrder) {
        if (sortBy == null || sortBy.isEmpty()) {
            return phones;
        }

        boolean ascending = "asc".equalsIgnoreCase(sortOrder);

        phones.sort((b1, b2) -> {
            int comparison = 0;
            switch (sortBy.toLowerCase()) {
                case "phoneName":
                    comparison = b1.getPhoneName().compareToIgnoreCase(b2.getPhoneName());
                    break;
                case "price":
                    BigDecimal price1 = b1.getPrice() != null ? b1.getPrice() : BigDecimal.ZERO;
                    BigDecimal price2 = b2.getPrice() != null ? b2.getPrice() : BigDecimal.ZERO;
                    comparison = price1.compareTo(price2);
                    break;
                default:
                    comparison = 0;
            }
            return ascending ? comparison : -comparison;
        });

        return phones;
    }

    @Override
    public Phones createPhone(PhonesDTO dto) {
        log.info("Creating new phone: {}", dto.getPhoneName());

        Phones phones = new Phones();
        phones.setPhoneName(dto.getPhoneName());
        phones.setCoverImageURL(dto.getCoverImageURL());
        phones.setPrice(dto.getPrice());
        phones.setPhoneDescription(dto.getPhoneDescription());

        Phones savedPhone = phonesRepository.save(phones);
        log.info("Phone created successfully with ID: {}", savedPhone.getPhoneId());

        return savedPhone;
    }

    @Override
    public Phones updatePhone(String PhoneId, PhonesDTO dto) {
        log.info("Updating phone: {}", PhoneId);

        Phones phone = phonesRepository.findById(PhoneId)
                .orElseThrow(() -> new ResourceNotFoundException( PhoneId));

        phone.setPhoneName(dto.getPhoneName());
        phone.setCoverImageURL(dto.getCoverImageURL());
        phone.setPrice(dto.getPrice());
        phone.setPhoneDescription(dto.getPhoneDescription());

        Phones updatedPhone = phonesRepository.save(phone);
        log.info("Phone updated successfully: {}", updatedPhone.getPhoneId());

        return updatedPhone;
    }

    @Override
    public void deletePhone(String PhoneId) {
        log.info("Deleting phone: {}", PhoneId);

        Phones phone = phonesRepository.findById(PhoneId)
                .orElseThrow(() -> new ResourceNotFoundException(PhoneId));

        phonesRepository.delete(phone);
        log.info("Phone deleted successfully: {}", PhoneId);
    }
}
