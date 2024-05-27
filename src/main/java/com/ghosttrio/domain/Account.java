package com.ghosttrio.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    @Version
    private Long version;

    @Builder
    public Account(double balance) {
        this.balance = balance;
    }
}
