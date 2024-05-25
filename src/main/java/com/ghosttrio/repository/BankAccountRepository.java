package com.ghosttrio.repository;

import com.ghosttrio.domain.BankAccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM BankAccountEntity b WHERE b.id = :id")
    BankAccountEntity findByIdWithPessimisticLock(Long id);
}