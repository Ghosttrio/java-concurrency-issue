package com.ghosttrio.dto;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccountWithLock {
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccountWithLock(int initialBalance) {
        this.balance = initialBalance;
    }

    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
