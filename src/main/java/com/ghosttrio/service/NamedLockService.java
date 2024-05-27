package com.ghosttrio.service;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NamedLockService {

    private final LoadAccountService loadAccountService;
    private final AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, double amount) {
        Account account = loadAccountService.loadAccount(id);
        account.withdraw(amount);
        accountRepository.saveAndFlush(account);
    }
}
