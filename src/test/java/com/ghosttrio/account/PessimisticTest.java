package com.ghosttrio.account;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import com.ghosttrio.service.AccountService;
import com.ghosttrio.service.LoadAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class PessimisticTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private LoadAccountService loadAccountService;
    @Autowired
    private AccountRepository accountRepository;

    private Long accountId;

    @BeforeEach
    void setUp() {
        accountId = accountRepository
                .saveAndFlush(Account.builder().balance(100).build()).getId();
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("비관적 락 테스트")
    void decrease_with_100_request() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdrawPessimistic(accountId, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Account account = loadAccountService.loadAccount(accountId);
        System.out.println(account.getBalance());
    }
}