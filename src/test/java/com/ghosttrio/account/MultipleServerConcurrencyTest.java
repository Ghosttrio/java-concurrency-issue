package com.ghosttrio.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultipleServerConcurrencyTest {

    @Test
    @DisplayName("단일 서버 요청")
    void 단일_서버() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    int port = 8080;
                    ResponseEntity<Void> forEntity = restTemplate.getForEntity(
                            "http://localhost:" + port + "/accounts/1/withdraw",
                            Void.class);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    @Test
    @DisplayName("다중 서버 요청")
    void 다중_서버() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int ii = i;
            executorService.submit(() -> {
                try {
                    int port = (ii % 2 == 0) ? 8080 : 8081;
                    ResponseEntity<Void> forEntity = restTemplate.getForEntity(
                            "http://localhost:" + port + "/accounts/1/withdraw",
                            Void.class);
                }  finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }


    @Test
    void 다중_서버_비관적락() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int ii = i;
            executorService.submit(() -> {
                try {
                    int port = (ii % 2 == 0) ? 8080 : 8081;
                    ResponseEntity<Void> forEntity = restTemplate.getForEntity(
                            "http://localhost:" + port + "/accounts/1/withdraw/pessimistic",
                            Void.class);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    @Test
    void 다중_서버_낙관적() throws InterruptedException {
        // given
        int threadCount = 100;
        RestTemplate restTemplate = new RestTemplate();
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int ii = i;
            executorService.submit(() -> {
                try {
                    int port = (ii % 2 == 0) ? 8080 : 8081;
                    ResponseEntity<Void> forEntity = restTemplate.getForEntity(
                            "http://localhost:" + port + "/accounts/1/withdraw/optimistic",
                            Void.class);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

}
