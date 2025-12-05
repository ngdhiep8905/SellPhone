package com.ptmhdv.SellPhone.Controller;

import com.ptmhdv.SellPhone.Service.PhonesService;
import com.ptmhdv.SellPhone.dto.PhonesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
@RequiredArgsConstructor
public class PhonesController {

    private final PhonesService phonesService;

    @GetMapping
    public List<PhonesDTO> getPhones(
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String keyword
    ) {
        return phonesService.search(brandId, keyword);
    }

    // Dự phòng: lấy detail 1 phone
    @GetMapping("/{id}")
    public PhonesDTO getPhone(@PathVariable Long id) {
        return phonesService.getById(id);
    }
}
