package com.ghosttrio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@Service
public class BankAccountService {

    private int balance = 0;

    @Autowired
    private LockRegistry lockRegistry;

    public void deposit(int amount) {
        Lock lock = lockRegistry.obtain("bankAccountLock");
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public int getBalance() {
        Lock lock = lockRegistry.obtain("bankAccountLock");
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
