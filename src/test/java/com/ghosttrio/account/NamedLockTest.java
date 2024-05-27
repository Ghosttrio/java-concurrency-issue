package com.ghosttrio.account;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import com.ghosttrio.service.AccountService;
import com.ghosttrio.service.LoadAccountService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class NamedLockTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private LoadAccountService loadAccountService;
    @Autowired
    private AccountRepository accountRepository;

    private Long accountId;

    @BeforeEach
    void setUp() {
        accountId = accountRepository.saveAndFlush(Account.builder().balance(100).build()).getId();
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    void decrease_with_100_request_name_lock() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdrawNamedLock(accountId, 1);
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Account account = loadAccountService.loadAccount(accountId);
        Assertions.assertThat(account.getBalance()).isEqualTo(0);
    }
}