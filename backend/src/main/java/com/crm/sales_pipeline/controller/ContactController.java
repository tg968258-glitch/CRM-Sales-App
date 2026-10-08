package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.ContactDto;
import com.crm.sales_pipeline.service.ContactService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@Tag(name = "Contact")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class ContactController {
    private final ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    @GetMapping
    public Page<ContactDto> getAllContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return contactService.getAllContacts(page,size);
    }
    @GetMapping("/{id}")
    public ContactDto getContactById(@PathVariable Integer id) {
        return contactService.getContactById(id);
    }
    @PostMapping
    public ContactDto createContact(@Valid @RequestBody ContactDto dto) {
        return contactService.createContact(dto);
    }
    @PutMapping("/{id}")
    public ContactDto updateContact(@PathVariable Integer id,
                                    @RequestBody ContactDto dto) {return contactService.updateContact(id, dto);}
    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public void deleteContact(@PathVariable Integer id) {contactService.deleteContact(id);}
}
