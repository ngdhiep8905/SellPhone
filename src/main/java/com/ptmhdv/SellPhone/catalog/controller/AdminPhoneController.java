package com.ptmhdv.SellPhone.catalog.controller;

import com.ptmhdv.SellPhone.catalog.dto.PhonesDTO;
import com.ptmhdv.SellPhone.catalog.entity.Phones;
import com.ptmhdv.SellPhone.catalog.service.PhoneService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/phones")
public class AdminPhoneController {
    private final PhoneService phoneService;

    public AdminPhoneController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @GetMapping
    public String listPhones(Model model) {
        List<Phones> phones = phoneService.getAllPhones();
        model.addAttribute("phones", phones);
        // TODO: Create admin/books/list.html template or use REST API
        return "redirect:/index.html"; // Temporary redirect
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("phone", new PhonesDTO());
        // TODO: Create admin/books/create.html template
        return "redirect:/index.html"; // Temporary redirect
    }

    @PostMapping("/create")
    public String createPhone(@Valid @ModelAttribute("phone") PhonesDTO dto, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "redirect:/index.html"; // Temporary redirect
        }

        try {
            phoneService.createPhone(dto);
            redirectAttributes.addFlashAttribute("success", "Phone created successfully");
            return "redirect:/index.html"; // Temporary redirect
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/index.html"; // Temporary redirect
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Phones phone = phoneService.getPhoneById(id);
        PhonesDTO dto = new PhonesDTO();
        dto.setPhoneName(phone.getPhoneName());
        dto.setPrice(phone.getPrice());
        dto.setCoverImageURL(phone.getCoverImageURL());
        dto.setPhoneDescription(phone.getPhoneDescription());

        model.addAttribute("phone", dto);
        model.addAttribute("phoneId", id);
        // TODO: Create admin/books/edit.html template
        return "redirect:/index.html"; // Temporary redirect
    }

    @PostMapping("/{id}/edit")
    public String updatePhone(@PathVariable String id, @Valid @ModelAttribute("phone") PhonesDTO dto, BindingResult result,
                             RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("phoneId", id);
            return "redirect:/index.html"; // Temporary redirect
        }

        try {
            phoneService.updatePhone(id, dto);
            redirectAttributes.addFlashAttribute("success", "Phone updated successfully");
            return "redirect:/index.html"; // Temporary redirect
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/index.html"; // Temporary redirect
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePhone(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            phoneService.deletePhone(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/index.html"; // Temporary redirect
    }

}
