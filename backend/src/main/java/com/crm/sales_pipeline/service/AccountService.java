package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.AccountDto;
import com.crm.sales_pipeline.entity.Account;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.repository.AccountRepository;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
    public Page<AccountDto> getAllAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("accId").descending()
        );
        return accountRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    public AccountDto getAccountById(Integer accountId) {
        Account account = findAccountById(accountId);
        return mapToResponse(account);
    }

    public AccountDto createAccount(AccountDto dto) {
        User owner = userRepository.findById(dto.getAccOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found"));
        Account account = new Account();
        account.setOwner(owner);
        account.setName(dto.getAccountName());
        account.setIndustry(dto.getIndustry());
        account.setPhone(dto.getPhoneNumber());
        account.setWebsite(dto.getWebsite());

        Account savedAccount = accountRepository.save(account);
        return mapToResponse(savedAccount);
    }
    public AccountDto updateAccount(Integer accountId, AccountDto dto) {
        Account account = findAccountById(accountId);

        if (dto.getAccountName() != null) {
            account.setName(dto.getAccountName());
        }
        if (dto.getIndustry() != null) {
            account.setIndustry(dto.getIndustry());
        }
        if (dto.getPhoneNumber() != null) {
            account.setPhone(dto.getPhoneNumber());
        }
        if (dto.getWebsite() != null) {
            account.setWebsite(dto.getWebsite());
        }
        Account updatedAccount = accountRepository.save(account);
        return mapToResponse(updatedAccount);
    }
    public void deleteAccount(Integer accountId) {
        Account account = findAccountById(accountId);
        accountRepository.delete(account);
    }

    private Account findAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
    }
    private AccountDto mapToResponse(Account account) {
        return new AccountDto(
                account.getAccId(),
                account.getOwner().getUid(),
                account.getName(),
                account.getIndustry(),
                account.getPhone(),
                account.getWebsite()
        );
    }

}
