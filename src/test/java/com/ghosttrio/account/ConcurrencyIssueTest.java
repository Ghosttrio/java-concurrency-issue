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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConcurrencyIssueTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private LoadAccountService loadAccountService;
    @Autowired
    private AccountRepository accountRepository;

    private Long accountId;

    @BeforeEach
    void setUp() {
        Account account = Account.builder().balance(100).build();
        accountId = accountRepository.saveAndFlush(account).getId();
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("기본 세팅 작동 확인")
    void depositAccount() {
        accountService.withdraw(accountId, 100);

        Account account = loadAccountService.loadAccount(accountId);

        assertThat(account.getBalance()).isEqualTo(0);
    }


    @Test
    @DisplayName("100개의 요청이 한 번에 들어오는 경우")
    void 동시성_이슈_발생_코드() throws InterruptedException {

        /**
         * 원하는 잔고 : 0
         * 실제 잔고 : 89
         */
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdraw(accountId, 1);
                } finally  {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Account account = loadAccountService.loadAccount(accountId);
        System.out.println(account.getBalance());
    }

    @Test
    @DisplayName("100개의 요청이 한 번에 들어오는 경우 with synchronized, transactional")
    void synchronized_키워드_사용_transactional() throws InterruptedException {

        /**
         * 원하는 잔고 : 0
         * 실제 잔고 : 47
         */
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdrawSyncTran(accountId, 1);
                } finally  {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Account account = loadAccountService.loadAccount(accountId);
        System.out.println(account.getBalance());
    }

    @Test
    @DisplayName("100개의 요청이 한 번에 들어오는 경우 with synchronized")
    void synchronized_키워드_사용() throws InterruptedException {
        /**
         * 원하는 잔고 : 0
         * 실제 잔고 : 0
         */
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdrawSync(accountId, 1);
                } finally  {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Account account = loadAccountService.loadAccount(accountId);

        assertThat(account.getBalance()).isEqualTo(0.0);
    }



    @Test
    void LOCK을_이용하여_해결() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdrawLock(accountId, 1);
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
        assertThat(account.getBalance()).isEqualTo(0);
    }

    @Test
    void LOCK_Registry를_이용하여_해결() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    accountService.withdrawLockRegistry(accountId, 1);
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
        assertThat(account.getBalance()).isEqualTo(0);
    }

}



