package com.bank.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType = AccountType.SAVINGS;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AccountType { SAVINGS, CHECKING, FIXED_DEPOSIT }

    public Account() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static AccountBuilder builder() { return new AccountBuilder(); }

    public static class AccountBuilder {
        private String accountNumber, accountName;
        private AccountType accountType = AccountType.SAVINGS;
        private BigDecimal balance = BigDecimal.ZERO;
        private BigDecimal interestRate = BigDecimal.ZERO;
        private Boolean isActive = true;
        private User user;

        public AccountBuilder accountNumber(String v) { this.accountNumber = v; return this; }
        public AccountBuilder accountName(String v) { this.accountName = v; return this; }
        public AccountBuilder accountType(AccountType v) { this.accountType = v; return this; }
        public AccountBuilder balance(BigDecimal v) { this.balance = v; return this; }
        public AccountBuilder interestRate(BigDecimal v) { this.interestRate = v; return this; }
        public AccountBuilder isActive(Boolean v) { this.isActive = v; return this; }
        public AccountBuilder user(User v) { this.user = v; return this; }

        public Account build() {
            Account a = new Account();
            a.accountNumber = accountNumber; a.accountName = accountName;
            a.accountType = accountType; a.balance = balance;
            a.interestRate = interestRate; a.isActive = isActive; a.user = user;
            return a;
        }
    }
}
