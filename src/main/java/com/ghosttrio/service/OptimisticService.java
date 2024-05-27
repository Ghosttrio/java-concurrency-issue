package com.ghosttrio.service;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptimisticService {
    private final LoadAccountService loadAccountService;
    private final AccountRepository accountRepository;

    @Transactional
    public void test(Long id, double amount){
        Account account = loadAccountService.loadAccountOptimistic(id);
        account.withdraw(amount);
        accountRepository.saveAndFlush(account);
    }
}
