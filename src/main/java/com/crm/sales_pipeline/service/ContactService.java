package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.ContactDto;
import com.crm.sales_pipeline.entity.Account;
import com.crm.sales_pipeline.entity.Contact;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.repository.AccountRepository;
import com.crm.sales_pipeline.repository.ContactRepository;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    public ContactService(ContactRepository contactRepository,
                          AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
    public ContactDto getContactById(Integer contactId) {
        Contact contact = findContactById(contactId);
        return mapToResponse(contact);
    }
    public Page<ContactDto> getAllContacts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("contactId").descending()
        );
        return contactRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public ContactDto createContact(ContactDto dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));

        User owner = userRepository.findById(dto.getContactOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found"));

        Contact contact = new Contact();
        contact.setAccount(account);
        contact.setOwner(owner);
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setJob_title(dto.getJobTitle());
        contact.setPhone(dto.getPhoneNumber());
        contact.setStatus(dto.getLifecycleStatus());

        Contact savedContact = contactRepository.save(contact);
        return mapToResponse(savedContact);
    }
    public ContactDto updateContact(Integer contactId, ContactDto dto) {
        Contact contact = findContactById(contactId);

        if (dto.getName() != null) {
            contact.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            contact.setEmail(dto.getEmail());
        }
        if (dto.getJobTitle() != null) {
            contact.setJob_title(dto.getJobTitle());
        }
        if (dto.getPhoneNumber() != null) {
            contact.setPhone(dto.getPhoneNumber());
        }
        if (dto.getLifecycleStatus() != null) {
            contact.setStatus(dto.getLifecycleStatus());
        }
        Contact updatedContact = contactRepository.save(contact);
        return mapToResponse(updatedContact);
    }
    public void deleteContact(Integer contactId) {
        Contact contact = findContactById(contactId);
        contactRepository.delete(contact);
    }
    private Contact findContactById(Integer id) {
        return contactRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contact not found"));
    }
    private ContactDto mapToResponse(Contact contact) {
        return new ContactDto(
                contact.getContactId(),
                contact.getAccount().getAccId(),
                contact.getOwner().getUid(),
                contact.getName(),
                contact.getEmail(),
                contact.getJob_title(),
                contact.getPhone(),
                contact.getStatus()
        );
    }
}
