package com.bank.dto;

import com.bank.entity.Transaction;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDtos {

    public static class DepositRequest {
        @NotNull private Long accountId;
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
        private String description;

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class WithdrawRequest {
        @NotNull private Long accountId;
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
        private String description;

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class TransferRequest {
        @NotNull private Long fromAccountId;
        @NotBlank private String toAccountNumber;
        @NotNull @DecimalMin("0.01") private BigDecimal amount;
        private String description;

        public Long getFromAccountId() { return fromAccountId; }
        public void setFromAccountId(Long fromAccountId) { this.fromAccountId = fromAccountId; }
        public String getToAccountNumber() { return toAccountNumber; }
        public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class TransactionResponse {
        private Long id;
        private String referenceNumber;
        private String transactionType;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private String description;
        private String accountNumber;
        private String accountName;
        private String relatedAccount;
        private LocalDateTime createdAt;

        public static TransactionResponse from(Transaction tx) {
            TransactionResponse r = new TransactionResponse();
            r.id = tx.getId();
            r.referenceNumber = tx.getReferenceNumber();
            r.transactionType = tx.getTransactionType().name();
            r.amount = tx.getAmount();
            r.balanceAfter = tx.getBalanceAfter();
            r.description = tx.getDescription();
            r.accountNumber = tx.getAccount().getAccountNumber();
            r.accountName = tx.getAccount().getAccountName();
            r.relatedAccount = tx.getRelatedAccount();
            r.createdAt = tx.getCreatedAt();
            return r;
        }

        public Long getId() { return id; }
        public String getReferenceNumber() { return referenceNumber; }
        public String getTransactionType() { return transactionType; }
        public BigDecimal getAmount() { return amount; }
        public BigDecimal getBalanceAfter() { return balanceAfter; }
        public String getDescription() { return description; }
        public String getAccountNumber() { return accountNumber; }
        public String getAccountName() { return accountName; }
        public String getRelatedAccount() { return relatedAccount; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }
}
