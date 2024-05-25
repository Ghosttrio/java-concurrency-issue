package com.ghosttrio.service;

import com.ghosttrio.domain.BankAccountEntity;
import com.ghosttrio.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BankServicePessimisticTest {

    @Autowired
    private BankServicePessimistic bankAccountService;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Test
    public void testDeposit() {
        BankAccountEntity newAccount = new BankAccountEntity();
        bankAccountRepository.save(newAccount);

        bankAccountService.deposit(newAccount.getId(), 100);

        BankAccountEntity updatedAccount = bankAccountRepository.findById(newAccount.getId()).get();
        System.out.println("기대값 ===> " + 100);
        System.out.println("실제값 ===> " + updatedAccount.getBalance());
    }
}