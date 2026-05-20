package com.bank.dto;

import com.bank.entity.Account;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AccountDtos {

    public static class CreateAccountRequest {
        @NotBlank @Size(max = 100)
        private String accountName;
        @NotNull
        private Account.AccountType accountType;
        @NotNull @DecimalMin("0.00")
        private BigDecimal initialDeposit;

        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
        public Account.AccountType getAccountType() { return accountType; }
        public void setAccountType(Account.AccountType accountType) { this.accountType = accountType; }
        public BigDecimal getInitialDeposit() { return initialDeposit; }
        public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
    }

    public static class AccountResponse {
        private Long id;
        private String accountNumber;
        private String accountName;
        private String accountType;
        private BigDecimal balance;
        private BigDecimal interestRate;
        private Boolean isActive;
        private LocalDateTime createdAt;

        public static AccountResponse from(Account account) {
            AccountResponse r = new AccountResponse();
            r.id = account.getId();
            r.accountNumber = account.getAccountNumber();
            r.accountName = account.getAccountName();
            r.accountType = account.getAccountType().name();
            r.balance = account.getBalance();
            r.interestRate = account.getInterestRate();
            r.isActive = account.getIsActive();
            r.createdAt = account.getCreatedAt();
            return r;
        }

        public Long getId() { return id; }
        public String getAccountNumber() { return accountNumber; }
        public String getAccountName() { return accountName; }
        public String getAccountType() { return accountType; }
        public BigDecimal getBalance() { return balance; }
        public BigDecimal getInterestRate() { return interestRate; }
        public Boolean getIsActive() { return isActive; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class DashboardResponse {
        private BigDecimal totalBalance;
        private long totalAccounts;
        private BigDecimal monthlyIncome;
        private BigDecimal monthlyExpense;
        private List<AccountResponse> accounts;
        private List<TransactionDtos.TransactionResponse> recentTransactions;

        public DashboardResponse(BigDecimal totalBalance, long totalAccounts,
                                  BigDecimal monthlyIncome, BigDecimal monthlyExpense,
                                  List<AccountResponse> accounts,
                                  List<TransactionDtos.TransactionResponse> recentTransactions) {
            this.totalBalance = totalBalance; this.totalAccounts = totalAccounts;
            this.monthlyIncome = monthlyIncome; this.monthlyExpense = monthlyExpense;
            this.accounts = accounts; this.recentTransactions = recentTransactions;
        }

        public BigDecimal getTotalBalance() { return totalBalance; }
        public long getTotalAccounts() { return totalAccounts; }
        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public BigDecimal getMonthlyExpense() { return monthlyExpense; }
        public List<AccountResponse> getAccounts() { return accounts; }
        public List<TransactionDtos.TransactionResponse> getRecentTransactions() { return recentTransactions; }
    }
}
