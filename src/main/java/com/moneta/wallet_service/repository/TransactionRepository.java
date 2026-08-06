package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletId(Long walletId);

    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.transactionType = 'EXPENSE' " +
            "GROUP BY t.category.name")
    List<Object[]> getExpenceBreakdownByCategory(Long walletId);

    List<Transaction> findByInvestmentSimulationId(Long investmentSimulationId);

    @Query("""
        SELECT SUM(t.amount) 
        FROM Transaction t 
        WHERE t.wallet.user.id = :userId 
          AND t.transactionType = com.moneta.wallet_service.enums.TransactionType.INCOME 
          AND MONTH(t.transactionDate) = :month 
          AND YEAR(t.transactionDate) = :year
    """)
    BigDecimal findTotalIncomeByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
        SELECT SUM(t.amount) 
        FROM Transaction t 
        WHERE t.wallet.user.id = :userId 
          AND t.transactionType = com.moneta.wallet_service.enums.TransactionType.EXPENSE 
          AND MONTH(t.transactionDate) = :month 
          AND YEAR(t.transactionDate) = :year
    """)
    BigDecimal findTotalExpenseByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );
}