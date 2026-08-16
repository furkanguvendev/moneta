package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.dto.response.MonthlyBreakdownResponse;
import com.moneta.wallet_service.dto.response.MonthlySummaryResponse;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.category WHERE t.wallet.id = :walletId")
    List<Transaction> findByWalletId(@Param("walletId") Long walletId);

    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.transactionType = com.moneta.wallet_service.enums.TransactionType.EXPENSE " +
            "GROUP BY t.category.name")
    List<Object[]> getExpenceBreakdownByCategory(@Param("walletId") Long walletId);

    List<Transaction> findByInvestmentSimulationId(Long investmentSimulationId);

    @Query("""
        SELECT new com.moneta.wallet_service.dto.response.MonthlySummaryResponse(
            COALESCE(SUM(CASE WHEN t.transactionType = com.moneta.wallet_service.enums.TransactionType.INCOME THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.transactionType = com.moneta.wallet_service.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END), 0)
        )
        FROM Transaction t 
        WHERE t.wallet.user.id = :userId 
          AND MONTH(t.transactionDate) = :month 
          AND YEAR(t.transactionDate) = :year
    """)
    MonthlySummaryResponse findMonthlySummaryByUserIdAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId " +
            "AND t.transactionType = :type " +
            "AND MONTH(t.transactionDate) = :month " +
            "AND YEAR(t.transactionDate) = :year")
    BigDecimal findMonthlyTotalByWalletIdAndType(
            @Param("walletId") Long walletId,
            @Param("type") TransactionType type,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT new com.moneta.wallet_service.dto.response.MonthlyBreakdownResponse(" +
            "YEAR(t.transactionDate), " +
            "MONTH(t.transactionDate), " +
            "SUM(CASE WHEN t.transactionType = com.moneta.wallet_service.enums.TransactionType.INCOME THEN t.amount ELSE 0 END), " +
            "SUM(CASE WHEN t.transactionType = com.moneta.wallet_service.enums.TransactionType.EXPENSE THEN t.amount ELSE 0 END)) " +
            "FROM Transaction t " +
            "WHERE t.wallet.id = :walletId " +
            "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) " +
            "ORDER BY YEAR(t.transactionDate) DESC, MONTH(t.transactionDate) DESC")
    List<MonthlyBreakdownResponse> findMonthlyBreakdownByWalletId(@Param("walletId") Long walletId);
}