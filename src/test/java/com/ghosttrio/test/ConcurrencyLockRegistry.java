package com.ghosttrio.test;

import com.ghosttrio.service.BankAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
public class ConcurrencyLockRegistry {

    @Autowired
    private BankAccountService bankAccountService;

    @Test
    public void testConcurrencyIssue() throws InterruptedException {
        int numberOfThreads = 100;
        int depositsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Runnable depositTask = () -> {
            for (int i = 0; i < depositsPerThread; i++) {
                bankAccountService.deposit(10);
            }
            latch.countDown();
        };

        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(depositTask);
            threads[i].start();
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 각 스레드가 100번의 입금을 하므로 최종 잔고는 100 * 1000이 되어야 함
        int expectedBalance = numberOfThreads * depositsPerThread * 10;
        int actualBalance = bankAccountService.getBalance();

        System.out.println("예상 금액 ===> " + expectedBalance);
        System.out.println("실제 금액 ===> " + actualBalance);
    }
}