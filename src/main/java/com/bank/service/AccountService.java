package com.bank.service;

import com.bank.dto.AccountDtos;
import com.bank.dto.TransactionDtos;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.exception.GlobalExceptionHandler.*;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    public AccountDtos.DashboardResponse getDashboard(String username) {
        User user = findUser(username);
        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        BigDecimal totalBalance = accountRepository.sumBalanceByUserId(user.getId());
        long totalAccounts = accountRepository.countActiveByUserId(user.getId());
        BigDecimal monthlyIncome = transactionRepository.sumCreditsByUserIdAndPeriod(user.getId(), startOfMonth, now);
        BigDecimal monthlyExpense = transactionRepository.sumDebitsByUserIdAndPeriod(user.getId(), startOfMonth, now);

        List<AccountDtos.AccountResponse> accounts = accountRepository
            .findByUserIdAndIsActiveTrue(user.getId())
            .stream().map(AccountDtos.AccountResponse::from).toList();

        List<TransactionDtos.TransactionResponse> recentTx = transactionRepository
            .findTop5ByAccountUserIdOrderByCreatedAtDesc(user.getId())
            .stream().map(TransactionDtos.TransactionResponse::from).toList();

        return new AccountDtos.DashboardResponse(
            totalBalance != null ? totalBalance : BigDecimal.ZERO,
            totalAccounts,
            monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO,
            monthlyExpense != null ? monthlyExpense : BigDecimal.ZERO,
            accounts, recentTx
        );
    }

    @Transactional
    public AccountDtos.AccountResponse createAccount(String username, AccountDtos.CreateAccountRequest request) {
        User user = findUser(username);

        BigDecimal interestRate = switch (request.getAccountType()) {
            case SAVINGS -> new BigDecimal("3.5");
            case FIXED_DEPOSIT -> new BigDecimal("7.0");
            default -> BigDecimal.ZERO;
        };

        Account account = Account.builder()
            .accountNumber(generateAccountNumber())
            .accountName(request.getAccountName())
            .accountType(request.getAccountType())
            .balance(request.getInitialDeposit())
            .interestRate(interestRate)
            .user(user)
            .build();

        account = accountRepository.save(account);

        if (request.getInitialDeposit().compareTo(BigDecimal.ZERO) > 0) {
            recordTransaction(account, Transaction.TransactionType.DEPOSIT,
                request.getInitialDeposit(), "Initial deposit", null);
        }

        return AccountDtos.AccountResponse.from(account);
    }

    public List<AccountDtos.AccountResponse> getMyAccounts(String username) {
        User user = findUser(username);
        return accountRepository.findByUserIdAndIsActiveTrue(user.getId())
            .stream().map(AccountDtos.AccountResponse::from).toList();
    }

    public AccountDtos.AccountResponse getAccount(String username, Long accountId) {
        User user = findUser(username);
        Account account = accountRepository.findByIdAndUserId(accountId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
        return AccountDtos.AccountResponse.from(account);
    }

    @Transactional
    public AccountDtos.AccountResponse closeAccount(String username, Long accountId) {
        User user = findUser(username);
        Account account = accountRepository.findByIdAndUserId(accountId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0)
            throw new IllegalStateException("Cannot close account with positive balance.");

        account.setIsActive(false);
        return AccountDtos.AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public TransactionDtos.TransactionResponse deposit(String username, TransactionDtos.DepositRequest request) {
        User user = findUser(username);
        Account account = getActiveAccountForUser(request.getAccountId(), user.getId());
        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);
        Transaction tx = recordTransaction(account, Transaction.TransactionType.DEPOSIT,
            request.getAmount(), request.getDescription() != null ? request.getDescription() : "Deposit", null);
        return TransactionDtos.TransactionResponse.from(tx);
    }

    @Transactional
    public TransactionDtos.TransactionResponse withdraw(String username, TransactionDtos.WithdrawRequest request) {
        User user = findUser(username);
        Account account = getActiveAccountForUser(request.getAccountId(), user.getId());

        if (account.getBalance().compareTo(request.getAmount()) < 0)
            throw new InsufficientFundsException("Insufficient funds. Available: " + account.getBalance());

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        Transaction tx = recordTransaction(account, Transaction.TransactionType.WITHDRAWAL,
            request.getAmount(), request.getDescription() != null ? request.getDescription() : "Withdrawal", null);
        return TransactionDtos.TransactionResponse.from(tx);
    }

    @Transactional
    public List<TransactionDtos.TransactionResponse> transfer(String username, TransactionDtos.TransferRequest request) {
        User user = findUser(username);
        Account fromAccount = getActiveAccountForUser(request.getFromAccountId(), user.getId());

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
            .orElseThrow(() -> new ResourceNotFoundException("Destination account not found: " + request.getToAccountNumber()));

        if (!toAccount.getIsActive())
            throw new AccountNotActiveException("Destination account is not active");

        if (fromAccount.getId().equals(toAccount.getId()))
            throw new IllegalArgumentException("Cannot transfer to the same account");

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0)
            throw new InsufficientFundsException("Insufficient funds. Available: " + fromAccount.getBalance());

        String desc = request.getDescription() != null ? request.getDescription() : "Transfer";
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction outTx = recordTransaction(fromAccount, Transaction.TransactionType.TRANSFER_OUT,
            request.getAmount(), desc + " to " + toAccount.getAccountNumber(), toAccount.getAccountNumber());
        Transaction inTx = recordTransaction(toAccount, Transaction.TransactionType.TRANSFER_IN,
            request.getAmount(), desc + " from " + fromAccount.getAccountNumber(), fromAccount.getAccountNumber());

        return List.of(
            TransactionDtos.TransactionResponse.from(outTx),
            TransactionDtos.TransactionResponse.from(inTx)
        );
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Account getActiveAccountForUser(Long accountId, Long userId) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
        if (!account.getIsActive())
            throw new AccountNotActiveException("Account is not active: " + accountId);
        return account;
    }

    private Transaction recordTransaction(Account account, Transaction.TransactionType type,
                                          BigDecimal amount, String description, String relatedAccount) {
        Transaction tx = Transaction.builder()
            .referenceNumber(generateReference())
            .transactionType(type)
            .amount(amount)
            .balanceAfter(account.getBalance())
            .description(description)
            .account(account)
            .relatedAccount(relatedAccount)
            .build();
        return transactionRepository.save(tx);
    }

    private String generateAccountNumber() {
        String num;
        do { num = "ACC" + System.currentTimeMillis() % 10000000L; }
        while (accountRepository.existsByAccountNumber(num));
        return num;
    }

    private String generateReference() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
