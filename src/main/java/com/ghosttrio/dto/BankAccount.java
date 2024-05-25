package com.ghosttrio.dto;

public class BankAccount {

    private int balance;

    // 계좌 초기 잔고 설정
    public BankAccount(int initialBalance) {
        this.balance = initialBalance;
    }

    // 계좌 입금
    public void deposit(int amount) {
        balance += amount;
    }

    // 계좌 잔고
    public int getBalance() {
        return balance;
    }


    public synchronized void syncDeposit(int amount) {
        balance += amount;
    }

    public synchronized int syncGetBalance() {
        return balance;
    }
}
