package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.AccountDto;
import com.crm.sales_pipeline.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @GetMapping
    public Page<AccountDto> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return accountService.getAllAccounts(page, size);
    }
    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable Integer id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public AccountDto createAccount(@Valid @RequestBody AccountDto dto) {
        return accountService.createAccount(dto);
    }

    @PutMapping("/{id}")
    public AccountDto updateAccount(@PathVariable Integer id, @RequestBody AccountDto dto)
    {return accountService.updateAccount(id, dto);}

        @DeleteMapping("/{id}")  @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
        public void deleteAccount(@PathVariable Integer id) {accountService.deleteAccount(id);
    }

    }

