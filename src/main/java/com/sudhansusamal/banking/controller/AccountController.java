package com.sudhansusamal.banking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhansusamal.banking.dto.AccountResponse;
import com.sudhansusamal.banking.dto.AmountRequest;
import com.sudhansusamal.banking.dto.CreateAccountRequest;
import com.sudhansusamal.banking.dto.TransactionResponse;
import com.sudhansusamal.banking.dto.TransferRequest;
import com.sudhansusamal.banking.service.BankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {

	private static final Logger log = LoggerFactory.getLogger(AccountController.class);

	private final BankingService bankingService;

	public AccountController(BankingService bankingService) {
		this.bankingService = bankingService;
	}

	@PostMapping("/accounts")
	public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
		log.info("POST /api/accounts request received for accountNumber={} with initialBalance={}",
				request.getAccountNumber(), request.getInitialBalance());
		AccountResponse response = bankingService.createAccount(request.getAccountNumber(),
				request.getInitialBalance());
		log.info("Account created successfully: accountNumber={}", response.getAccountNumber());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/accounts/{accountNumber}/balance")
	public ResponseEntity<AccountResponse> getBalance(@PathVariable String accountNumber) {
		log.info("GET /api/accounts/{}/balance request received", accountNumber);
		AccountResponse response = bankingService.getBalance(accountNumber);
		log.info("Balance retrieved for accountNumber={}: balance={}", accountNumber, response.getBalance());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/accounts/{accountNumber}/deposit")
	public ResponseEntity<AccountResponse> deposit(@PathVariable String accountNumber,
			@Valid @RequestBody AmountRequest request) {
		log.info("POST /api/accounts/{}/deposit request received with amount={}", accountNumber, request.getAmount());
		AccountResponse response = bankingService.deposit(accountNumber, request.getAmount());
		log.info("Deposit completed for accountNumber={}. New balance={}", accountNumber, response.getBalance());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/accounts/{accountNumber}/withdraw")
	public ResponseEntity<AccountResponse> withdraw(@PathVariable String accountNumber,
			@Valid @RequestBody AmountRequest request) {
		log.info("POST /api/accounts/{}/withdraw request received with amount={}", accountNumber, request.getAmount());
		AccountResponse response = bankingService.withdraw(accountNumber, request.getAmount());
		log.info("Withdrawal completed for accountNumber={}. New balance={}", accountNumber, response.getBalance());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/accounts/transfer")
	public ResponseEntity<AccountResponse> transfer(@Valid @RequestBody TransferRequest request) {
		log.info("POST /api/accounts/transfer request received from={} to={} amount={}", request.getFromAccountNumber(),
				request.getToAccountNumber(), request.getAmount());
		AccountResponse response = bankingService.transfer(request.getFromAccountNumber(), request.getToAccountNumber(),
				request.getAmount());
		log.info("Transfer completed from={} to={} for amount={}", request.getFromAccountNumber(),
				request.getToAccountNumber(), request.getAmount());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/accounts/{accountNumber}/transactions")
	public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable String accountNumber) {
		log.info("GET /api/accounts/{}/transactions request received", accountNumber);
		List<TransactionResponse> response = bankingService.getTransactionHistory(accountNumber);
		log.info("Retrieved {} transactions for accountNumber={}", response.size(), accountNumber);
		return ResponseEntity.ok(response);
	}
}
