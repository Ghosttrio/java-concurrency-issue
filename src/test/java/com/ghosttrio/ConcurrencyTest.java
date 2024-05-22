package com.ghosttrio;

import org.junit.jupiter.api.Test;

public class ConcurrencyTest {

    public static class BankAccount{
        private int balance = 1000;

        public void withdraw(int amount){
            if(balance >= amount){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                balance -= amount;
            }
        }

        public int getBalance(){
            return balance;
        }
    }

    public static class BankAccountWithSync{
        private int balance = 1000;

        public synchronized void withdraw(int amount){
            if(balance >= amount){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                balance -= amount;
            }
        }

        public int getBalance(){
            return balance;
        }
    }

    @Test
    void issue() throws InterruptedException {
        BankAccount account = new BankAccount();

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                account.withdraw(10);
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Final balance: " + account.getBalance());
    }

    @Test
    void issueWithSync() throws InterruptedException {
        BankAccountWithSync account = new BankAccountWithSync();

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                account.withdraw(10);
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Final balance: " + account.getBalance());
    }

}
