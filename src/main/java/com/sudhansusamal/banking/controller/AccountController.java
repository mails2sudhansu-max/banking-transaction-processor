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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {

	private final BankingService bankingService;

	public AccountController(BankingService bankingService) {
		this.bankingService = bankingService;
	}

	@PostMapping("/accounts")
	public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
		AccountResponse response = bankingService.createAccount(request.getAccountNumber(),
				request.getInitialBalance());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/accounts/{accountNumber}/balance")
	public ResponseEntity<AccountResponse> getBalance(@PathVariable String accountNumber) {
		return ResponseEntity.ok(bankingService.getBalance(accountNumber));
	}

	@PostMapping("/accounts/{accountNumber}/deposit")
	public ResponseEntity<AccountResponse> deposit(@PathVariable String accountNumber,
			@Valid @RequestBody AmountRequest request) {
		return ResponseEntity.ok(bankingService.deposit(accountNumber, request.getAmount()));
	}

	@PostMapping("/accounts/{accountNumber}/withdraw")
	public ResponseEntity<AccountResponse> withdraw(@PathVariable String accountNumber,
			@Valid @RequestBody AmountRequest request) {
		return ResponseEntity.ok(bankingService.withdraw(accountNumber, request.getAmount()));
	}

	@PostMapping("/accounts/transfer")
	public ResponseEntity<AccountResponse> transfer(@Valid @RequestBody TransferRequest request) {
		return ResponseEntity.ok(bankingService.transfer(request.getFromAccountNumber(), request.getToAccountNumber(),
				request.getAmount()));
	}

	@GetMapping("/accounts/{accountNumber}/transactions")
	public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable String accountNumber) {
		return ResponseEntity.ok(bankingService.getTransactionHistory(accountNumber));
	}
}
