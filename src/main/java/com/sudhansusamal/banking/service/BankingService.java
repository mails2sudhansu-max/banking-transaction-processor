package com.sudhansusamal.banking.service;

import com.sudhansusamal.banking.dto.AccountResponse;
import com.sudhansusamal.banking.dto.TransactionResponse;
import com.sudhansusamal.banking.exception.BusinessException;
import com.sudhansusamal.banking.model.Account;
import com.sudhansusamal.banking.model.Transaction;
import com.sudhansusamal.banking.model.TransactionType;
import com.sudhansusamal.banking.repository.AccountRepository;
import com.sudhansusamal.banking.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingService {

	private static final Logger log = LoggerFactory.getLogger(BankingService.class);

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	public BankingService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
	}

	@Transactional
	public AccountResponse createAccount(String accountNumber, BigDecimal initialBalance) {
		log.info("Creating account: accountNumber={}, initialBalance={}", accountNumber, initialBalance);
		validateAccountNumber(accountNumber);
		if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
			log.warn("Invalid initial balance for accountNumber={}: {}", accountNumber, initialBalance);
			throw new BusinessException("Initial balance cannot be negative");
		}

		if (accountRepository.findByAccountNumber(accountNumber).isPresent()) {
			log.warn("Account already exists: {}", accountNumber);
			throw new BusinessException("Account already exists: " + accountNumber);
		}

		Account account = new Account(accountNumber, initialBalance);
		accountRepository.save(account);
		log.info("Created account {} with balance {}", accountNumber, initialBalance);
		return mapToAccountResponse(account);
	}

	@Transactional
	public AccountResponse deposit(String accountNumber, BigDecimal amount) {
		log.info("Processing deposit: accountNumber={}, amount={}", accountNumber, amount);
		Account account = getAccount(accountNumber);
		validatePositiveAmount(amount, "Deposit amount");

		BigDecimal newBalance = account.getBalance().add(amount);
		account.setBalance(newBalance);
		transactionRepository.save(new Transaction(accountNumber, null, TransactionType.DEPOSIT, amount, newBalance));
		log.info("Deposited {} into account {}. New balance {}", amount, accountNumber, newBalance);
		return mapToAccountResponse(account);
	}

	@Transactional
	public AccountResponse withdraw(String accountNumber, BigDecimal amount) {
		log.info("Processing withdrawal: accountNumber={}, amount={}", accountNumber, amount);
		Account account = getAccount(accountNumber);
		validatePositiveAmount(amount, "Withdrawal amount");

		if (account.getBalance().compareTo(amount) < 0) {
			log.warn("Insufficient funds for withdrawal: accountNumber={}, requestedAmount={}, balance={}",
					accountNumber, amount, account.getBalance());
			throw new BusinessException("Insufficient funds for account: " + accountNumber);
		}

		BigDecimal newBalance = account.getBalance().subtract(amount);
		account.setBalance(newBalance);
		transactionRepository
				.save(new Transaction(accountNumber, null, TransactionType.WITHDRAWAL, amount, newBalance));
		log.info("Withdrew {} from account {}. New balance {}", amount, accountNumber, newBalance);
		return mapToAccountResponse(account);
	}

	@Transactional
	public AccountResponse transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
		log.info("Processing transfer: fromAccountNumber={}, toAccountNumber={}, amount={}", fromAccountNumber,
				toAccountNumber, amount);
		validateAccountNumber(fromAccountNumber);
		validateAccountNumber(toAccountNumber);
		validatePositiveAmount(amount, "Transfer amount");

		if (fromAccountNumber.equals(toAccountNumber)) {
			log.warn("Transfer attempted with same source and destination account: {}", fromAccountNumber);
			throw new BusinessException("Source and destination accounts must be different");
		}

		Account source = getAccount(fromAccountNumber);
		Account target = getAccount(toAccountNumber);

		if (source.getBalance().compareTo(amount) < 0) {
			log.warn("Insufficient funds for transfer: fromAccountNumber={}, requestedAmount={}, balance={}",
					fromAccountNumber, amount, source.getBalance());
			throw new BusinessException("Insufficient funds for account: " + fromAccountNumber);
		}

		BigDecimal sourceBalanceAfter = source.getBalance().subtract(amount);
		BigDecimal targetBalanceAfter = target.getBalance().add(amount);

		source.setBalance(sourceBalanceAfter);
		target.setBalance(targetBalanceAfter);

		transactionRepository.save(new Transaction(fromAccountNumber, toAccountNumber, TransactionType.TRANSFER_OUT,
				amount, sourceBalanceAfter));
		transactionRepository.save(new Transaction(toAccountNumber, fromAccountNumber, TransactionType.TRANSFER_IN,
				amount, targetBalanceAfter));

		log.info("Transferred {} from {} to {}. Source balance after={}, Target balance after={}", amount,
				fromAccountNumber, toAccountNumber, sourceBalanceAfter, targetBalanceAfter);
		return mapToAccountResponse(source);
	}

	@Transactional(readOnly = true)
	public AccountResponse getBalance(String accountNumber) {
		log.info("Fetching balance for accountNumber={}", accountNumber);
		return mapToAccountResponse(getAccount(accountNumber));
	}

	@Transactional(readOnly = true)
	public List<TransactionResponse> getTransactionHistory(String accountNumber) {
		log.info("Fetching transaction history for accountNumber={}", accountNumber);
		getAccount(accountNumber);
		return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber).stream()
				.map(this::mapToTransactionResponse).collect(Collectors.toList());
	}

	private Account getAccount(String accountNumber) {
		validateAccountNumber(accountNumber);
		return accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> {
			log.warn("Account not found for accountNumber={}", accountNumber);
			return new BusinessException("Account not found: " + accountNumber);
		});
	}

	private void validateAccountNumber(String accountNumber) {
		if (accountNumber == null || accountNumber.isBlank()) {
			log.warn("Account number validation failed: {}", accountNumber);
			throw new BusinessException("Account number is required");
		}
	}

	private void validatePositiveAmount(BigDecimal amount, String fieldName) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
			log.warn("{} validation failed: {}", fieldName, amount);
			throw new BusinessException(fieldName + " must be greater than zero");
		}
	}

	private AccountResponse mapToAccountResponse(Account account) {
		return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance(),
				account.getCreatedAt());
	}

	private TransactionResponse mapToTransactionResponse(Transaction transaction) {
		return new TransactionResponse(transaction.getId(), transaction.getAccountNumber(),
				transaction.getCounterpartyAccountNumber(), transaction.getType(), transaction.getAmount(),
				transaction.getBalanceAfter(), transaction.getTimestamp());
	}
}