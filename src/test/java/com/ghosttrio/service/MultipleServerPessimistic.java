package com.ghosttrio.service;

import com.ghosttrio.domain.BankAccountEntity;
import com.ghosttrio.dto.BankAccount;
import com.ghosttrio.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MultipleServerPessimistic {

    @Autowired
    private BankServicePessimistic bankAccountService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    private BankAccountEntity account;

    @BeforeEach
    public void setup() {
        account = new BankAccountEntity();
        account.deposit(100);
        bankAccountRepository.save(account);
    }

    @Test
    public void testConcurrentDeposits() throws InterruptedException {
        int numberOfThreads = 10;
        int depositAmount = 50;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> bankAccountService.deposit(account.getId(), depositAmount));
        }

        service.shutdown();
        assertTrue(service.awaitTermination(1, TimeUnit.MINUTES));

        // 재조회하여 잔액 확인
        BankAccountEntity updatedAccount = bankAccountRepository.findById(account.getId()).orElse(null);
        assertNotNull(updatedAccount);
        // 기대 잔액 계산: 초기 잔액 + (입금액 * 스레드 수)
        assertEquals(100.00 + depositAmount * numberOfThreads, updatedAccount.getBalance(), 0.01);
    }
}
