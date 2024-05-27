package com.ghosttrio.controller;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import com.ghosttrio.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @GetMapping("/accounts")
    @Transactional
    public void createAccount() {
        accountRepository.save(Account.builder().balance(100).build());
    }

    @GetMapping("/accounts/{id}/withdraw")
    public void withdrawSync(@PathVariable Long id) {
        accountService.withdrawSync(id, 1);
    }

    @GetMapping("/accounts/{id}/withdraw/pessimistic")
    public void withdrawPessimistic(@PathVariable Long id) {
        accountService.withdrawPessimistic(id, 1);
    }

    @GetMapping("/accounts/{id}/withdraw/optimistic")
    public void withdrawOptimistic(@PathVariable Long id) {
        accountService.withdrawOptimistic(id, 1);
    }
}