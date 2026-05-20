package com.bank.repository;

import com.bank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.createdAt DESC")
    Page<Transaction> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.transactionType = :type ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdAndType(
        @Param("userId") Long userId,
        @Param("type") Transaction.TransactionType type
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId AND t.transactionType IN ('DEPOSIT', 'TRANSFER_IN') " +
           "AND t.createdAt >= :from AND t.createdAt <= :to")
    BigDecimal sumCreditsByUserIdAndPeriod(
        @Param("userId") Long userId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId AND t.transactionType IN ('WITHDRAWAL', 'TRANSFER_OUT') " +
           "AND t.createdAt >= :from AND t.createdAt <= :to")
    BigDecimal sumDebitsByUserIdAndPeriod(
        @Param("userId") Long userId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    List<Transaction> findTop5ByAccountUserIdOrderByCreatedAtDesc(Long userId);
}
