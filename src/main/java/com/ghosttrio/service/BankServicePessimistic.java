package com.ghosttrio.service;

import com.ghosttrio.domain.BankAccountEntity;
import com.ghosttrio.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankServicePessimistic {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    /**
     * 이 서비스 메소드는 트랜잭션을 시작하고,
     * 지정된 ID의 계좌에 대해 비관적 락을 걸어 동시에 다른 트랜잭션이
     * 같은 계좌를 수정하지 못하도록 합니다.
     * 이러한 방식으로 동시성 문제를 해결합니다.
     */
    @Transactional
    public void deposit(Long accountId, int amount) {
        BankAccountEntity account = bankAccountRepository.findByIdWithPessimisticLock(accountId);
        account.deposit(amount);
        bankAccountRepository.save(account);
    }
}