package com.bank.controller;

import com.bank.dto.AccountDtos;
import com.bank.dto.TransactionDtos;
import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BankController {

    @Autowired private AccountService accountService;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<AccountDtos.DashboardResponse> getDashboard(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(accountService.getDashboard(user.getUsername()));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDtos.AccountResponse>> getAccounts(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(accountService.getMyAccounts(user.getUsername()));
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountDtos.AccountResponse> getAccount(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(user.getUsername(), id));
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountDtos.AccountResponse> createAccount(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AccountDtos.CreateAccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(user.getUsername(), request));
    }

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<AccountDtos.AccountResponse> closeAccount(
            @AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        return ResponseEntity.ok(accountService.closeAccount(user.getUsername(), id));
    }

    @PostMapping("/transactions/deposit")
    public ResponseEntity<TransactionDtos.TransactionResponse> deposit(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody TransactionDtos.DepositRequest request) {
        return ResponseEntity.ok(accountService.deposit(user.getUsername(), request));
    }

    @PostMapping("/transactions/withdraw")
    public ResponseEntity<TransactionDtos.TransactionResponse> withdraw(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody TransactionDtos.WithdrawRequest request) {
        return ResponseEntity.ok(accountService.withdraw(user.getUsername(), request));
    }

    @PostMapping("/transactions/transfer")
    public ResponseEntity<List<TransactionDtos.TransactionResponse>> transfer(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody TransactionDtos.TransferRequest request) {
        return ResponseEntity.ok(accountService.transfer(user.getUsername(), request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionDtos.TransactionResponse>> getTransactions(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Long userId = userRepository.findByUsername(user.getUsername()).orElseThrow().getId();
        Page<Transaction> txPage = accountId != null
            ? transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, pageable)
            : transactionRepository.findAllByUserId(userId, pageable);

        return ResponseEntity.ok(txPage.map(TransactionDtos.TransactionResponse::from));
    }
}
