package com.ghosttrio.test;


import com.ghosttrio.dto.BankAccount;
import com.ghosttrio.dto.BankAccountWithLock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;


public class ConcurrencyIssueTest {

    @Test
    @DisplayName("동시성 이슈가 발생하는 테스트 코드")
    public void testConcurrencyIssue() throws InterruptedException {
        BankAccount account = new BankAccount(0);
        int numberOfThreads = 100;
        int depositsPerThread = 100;
        //모든 스레드가 작업을 완료할 때까지 대기할 CountDownLatch를 생성합니다.
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 각 스레드가 100번의 입금을 수행하는 작업을 정의합니다.
        Runnable depositTask = () -> {
            for (int i = 0; i < depositsPerThread; i++) {
                account.deposit(10);
            }
            // 작업이 완료되었음을 CountDownLatch에 알립니다.
            latch.countDown();
        };

        // 입금을 수행
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(depositTask);
            threads[i].start();
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 각 스레드가 100번의 입금을 하므로 최종 잔고는 100 * 1000이 되어야 함
        int expectedBalance = numberOfThreads * depositsPerThread * 10;
        int actualBalance = account.getBalance();

        System.out.println("예상 금액 ===> " + expectedBalance);
        System.out.println("실제 금액 ===> " + actualBalance);
    }


    @Test
    @DisplayName("synchronized 키워드로 동시성 이슈 해결, 다중 서버에서는 동시성 이슈를 해결하지 못한다.")
    public void synchronizedTest() throws InterruptedException {
        BankAccount account = new BankAccount(0);
        int numberOfThreads = 100;
        int depositsPerThread = 100;
        //모든 스레드가 작업을 완료할 때까지 대기할 CountDownLatch를 생성합니다.
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 각 스레드가 100번의 입금을 수행하는 작업을 정의합니다.
        Runnable depositTask = () -> {
            for (int i = 0; i < depositsPerThread; i++) {
                account.syncDeposit(10);
            }
            // 작업이 완료되었음을 CountDownLatch에 알립니다.
            latch.countDown();
        };

        // 입금을 수행
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(depositTask);
            threads[i].start();
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 각 스레드가 100번의 입금을 하므로 최종 잔고는 100 * 1000이 되어야 함
        int expectedBalance = numberOfThreads * depositsPerThread * 10;
        int actualBalance = account.syncGetBalance();

        System.out.println("예상 금액 ===> " + expectedBalance);
        System.out.println("실제 금액 ===> " + actualBalance);
    }


    @Test
    @DisplayName("java Lock 인터페이스를 사용하여 해결, 다중 서버 해결 못함")
    public void testLock() throws InterruptedException {
        BankAccountWithLock account = new BankAccountWithLock(0);
        int numberOfThreads = 100;
        int depositsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Runnable depositTask = () -> {
            for (int i = 0; i < depositsPerThread; i++) {
                account.deposit(10);
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
        int actualBalance = account.getBalance();

        System.out.println("예상 금액 ===> " + expectedBalance);
        System.out.println("실제 금액 ===> " + actualBalance);
    }
}
