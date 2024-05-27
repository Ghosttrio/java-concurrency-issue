package com.ghosttrio.service;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadAccountService {

    private final AccountRepository accountRepository;

    /**
     * 1. 기본 조회
     */
    public Account loadAccount(Long id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 계좌가 없습니다."));
    }

    /**
     * 2. 비관적 락
     */
    public Account loadAccountPessimistic(Long id){
        return accountRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new IllegalStateException("해당 계좌가 없습니다."));
    }

    /**
     * 3. 낙관적 락
     */
    public Account loadAccountOptimistic(Long id){
        return accountRepository.findByIdWithOptimisticLock(id)
                .orElseThrow(() -> new IllegalStateException("해당 계좌가 없습니다."));
    }

}
