package com.bank.repository;

import com.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserIdAndIsActiveTrue(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByIdAndUserId(Long id, Long userId);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    BigDecimal sumBalanceByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    long countActiveByUserId(@Param("userId") Long userId);
}
