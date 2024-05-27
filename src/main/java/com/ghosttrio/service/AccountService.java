package com.ghosttrio.service;

import com.ghosttrio.domain.Account;
import com.ghosttrio.repository.AccountRepository;
import com.ghosttrio.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final LoadAccountService loadAccountService;
    private final AccountRepository accountRepository;
    private final NamedLockService namedLockService;

    /**
     * 1. 출금
     */
    @Transactional
    public void withdraw(Long id, double amount) {
        Account account = loadAccountService.loadAccount(id);
        account.withdraw(amount);
        accountRepository.saveAndFlush(account);
    }

    /**
     * 2. 출금 @Transactional, synchronized
     */
    @Transactional
    public synchronized void withdrawSyncTran(Long id, double amount) {
        Account account = loadAccountService.loadAccount(id);
        account.withdraw(amount);
        accountRepository.saveAndFlush(account);
    }

    /**
     * 3. 출금  synchronized
     */
    public synchronized void withdrawSync(Long id, double amount) {
        Account account = loadAccountService.loadAccount(id);
        account.withdraw(amount);
        accountRepository.saveAndFlush(account);
    }


    /**
     * 4. Lock
     */
    private ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

    public void withdrawLock(Long id, double amount) throws InterruptedException {
        Lock lock = locks.computeIfAbsent(String.valueOf(id), key -> new ReentrantLock());
        boolean acquiredLock = lock.tryLock(3, TimeUnit.SECONDS);
        if (!acquiredLock) {
            throw new RuntimeException("Lock 획득 실패");
        }
        try {
            Account account = loadAccountService.loadAccount(id);
            account.withdraw(amount);
            accountRepository.saveAndFlush(account);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 5. LockRegistry
     */
    private final LockRegistry lockRegistry;

    public void withdrawLockRegistry(Long id, double amount) throws InterruptedException {
        lockRegistry.executeLocked(String.valueOf(id), () -> {
            Account account = loadAccountService.loadAccount(id);
            account.withdraw(amount);
            accountRepository.saveAndFlush(account);
        });
    }

    /**
     * 6. 비관적 락
     */

    @Transactional
    public void withdrawPessimistic(Long id, double amount) {
        Account account = loadAccountService.loadAccountPessimistic(id);
        account.withdraw(amount);
        accountRepository.saveAndFlush(account);
    }

    /**
     * 7. 낙관적 락
     */

    private final OptimisticService optimisticService;

    public void withdrawOptimistic(Long id, double amount) {
        while (true) {
            try {
                optimisticService.test(id, amount);
                break;
            } catch (Exception e) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }


    /**
     * 8. Named Lock
     */

    @Transactional
    public void withdrawNamedLock(Long id, double amount){
        try {
            Integer acquiredLock = accountRepository.getLock(String.valueOf(id));
            if (acquiredLock != 1) {
                throw new RuntimeException("Lock 획득에 실패했습니다. [id: %d]".formatted(id));
            }
            namedLockService.decrease(id, amount);
        } finally {
            accountRepository.releaseLock(String.valueOf(id));
        }
    }

    /**
     * 9. Redis Lettuce
     */


    private final RedisLockRepository redisLockRepository;

    public void withdrawLettuce(Long id, double amount) {
        while (!redisLockRepository.lock(id)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        try {
            withdraw(id, amount);
        } finally {
            redisLockRepository.unlock(id);
        }
    }

    /**
     * 10. Redis Redisson
     */
    private final RedissonClient redissonClient;

    public void withdrawRedisson(Long id, double amount) {
        RLock lock = redissonClient.getLock(id.toString());

        try {
            boolean acquireLock = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!acquireLock) {
                System.out.println("Lock 획득 실패");
                return;
            }
            withdraw(id, amount);
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

}
