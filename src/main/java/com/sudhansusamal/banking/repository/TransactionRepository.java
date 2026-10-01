package com.sudhansusamal.banking.repository;

import com.sudhansusamal.banking.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountNumberOrderByTimestampDesc(String accountNumber);
}
