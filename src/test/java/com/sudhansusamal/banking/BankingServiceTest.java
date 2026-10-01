package com.sudhansusamal.banking;

import com.sudhansusamal.banking.dto.AccountResponse;
import com.sudhansusamal.banking.repository.AccountRepository;
import com.sudhansusamal.banking.service.BankingService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BankingServiceTest {

    @Autowired
    private BankingService bankingService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldPersistDepositWithdrawalAndTransferLifecycle() {
        AccountResponse created = bankingService.createAccount("ACC-100", new BigDecimal("1000.00"));
        assertThat(created.getBalance()).isEqualByComparingTo("1000.00");

        AccountResponse afterDeposit = bankingService.deposit("ACC-100", new BigDecimal("250.50"));
        assertThat(afterDeposit.getBalance()).isEqualByComparingTo("1250.50");

        AccountResponse afterWithdrawal = bankingService.withdraw("ACC-100", new BigDecimal("150.25"));
        assertThat(afterWithdrawal.getBalance()).isEqualByComparingTo("1100.25");

        bankingService.createAccount("ACC-200", new BigDecimal("500.00"));
        AccountResponse afterTransfer = bankingService.transfer("ACC-100", "ACC-200", new BigDecimal("200.00"));
        assertThat(afterTransfer.getBalance()).isEqualByComparingTo("900.25");

        assertThat(bankingService.getBalance("ACC-200").getBalance()).isEqualByComparingTo("700.00");
        assertThat(bankingService.getTransactionHistory("ACC-100")).hasSize(3);
    }

    @Test
    void shouldRejectNegativeInitialBalance() {
        try {
            bankingService.createAccount("ACC-NEG", new BigDecimal("-10.00"));
        } catch (Exception ex) {
            assertThat(ex.getMessage()).contains("negative");
            return;
        }
        throw new AssertionError("Expected exception for negative initial balance");
    }
}
