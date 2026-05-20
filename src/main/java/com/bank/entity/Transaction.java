package com.bank.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 30)
    private String referenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    @Column(length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "related_account", length = 20)
    private String relatedAccount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT }

    public Transaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public String getRelatedAccount() { return relatedAccount; }
    public void setRelatedAccount(String relatedAccount) { this.relatedAccount = relatedAccount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static TransactionBuilder builder() { return new TransactionBuilder(); }

    public static class TransactionBuilder {
        private String referenceNumber, description, relatedAccount;
        private TransactionType transactionType;
        private BigDecimal amount, balanceAfter;
        private Account account;

        public TransactionBuilder referenceNumber(String v) { this.referenceNumber = v; return this; }
        public TransactionBuilder transactionType(TransactionType v) { this.transactionType = v; return this; }
        public TransactionBuilder amount(BigDecimal v) { this.amount = v; return this; }
        public TransactionBuilder balanceAfter(BigDecimal v) { this.balanceAfter = v; return this; }
        public TransactionBuilder description(String v) { this.description = v; return this; }
        public TransactionBuilder account(Account v) { this.account = v; return this; }
        public TransactionBuilder relatedAccount(String v) { this.relatedAccount = v; return this; }

        public Transaction build() {
            Transaction t = new Transaction();
            t.referenceNumber = referenceNumber; t.transactionType = transactionType;
            t.amount = amount; t.balanceAfter = balanceAfter; t.description = description;
            t.account = account; t.relatedAccount = relatedAccount;
            return t;
        }
    }
}
