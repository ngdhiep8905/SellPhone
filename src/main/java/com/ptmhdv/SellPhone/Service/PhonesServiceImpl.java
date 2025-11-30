package com.ptmhdv.SellPhone.Service;

import com.ptmhdv.SellPhone.Entity.Brands;
import com.ptmhdv.SellPhone.Entity.Phones;
import com.ptmhdv.SellPhone.Mapper.PhonesMapper;
import com.ptmhdv.SellPhone.Repository.BrandsRepository;
import com.ptmhdv.SellPhone.Repository.PhonesRepository;
import com.ptmhdv.SellPhone.dto.PhonesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhonesServiceImpl implements PhonesService {

    private final PhonesRepository phonesRepository;
    private final BrandsRepository brandsRepository;

    @Override
    public List<PhonesDTO> search(Long brandId, String keyword) {

        // Lấy tất cả phones
        List<Phones> phones = phonesRepository.findAll();

        return phones.stream()
                .filter(p -> brandId == null || p.getBrand().getBrandId().equals(brandId))
                .filter(p -> keyword == null ||
                        p.getPhoneName().toLowerCase().contains(keyword.toLowerCase()))
                .map(PhonesMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhonesDTO getById(Long id) {
        Phones phone = phonesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phone not found"));
        return PhonesMapper.toDTO(phone);
    }

    @Override
    public PhonesDTO create(PhonesDTO dto) {

        Brands brand = brandsRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Phones phone = new Phones();
        phone.setPhoneName(dto.getPhoneName());
        phone.setPrice(dto.getPrice());
        phone.setCoverImageURL(dto.getCoverImageURL());
        phone.setPhoneDescription(dto.getPhoneDescription());
        phone.setBrand(brand);

        Phones saved = phonesRepository.save(phone);
        return PhonesMapper.toDTO(saved);
    }

    @Override
    public PhonesDTO update(Long id, PhonesDTO dto) {

        Phones exist = phonesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        Brands brand = brandsRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        // Update fields
        exist.setPhoneName(dto.getPhoneName());
        exist.setPrice(dto.getPrice());
        exist.setCoverImageURL(dto.getCoverImageURL());
        exist.setPhoneDescription(dto.getPhoneDescription());
        exist.setBrand(brand);

        Phones saved = phonesRepository.save(exist);
        return PhonesMapper.toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Phones exist = phonesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        phonesRepository.delete(exist);
    }
}
