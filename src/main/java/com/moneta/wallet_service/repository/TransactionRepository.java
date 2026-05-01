package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);

    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.transactionType = 'EXPENSE' " +
            "GROUP BY t.category.name")
    List<Object[]> getExpenceBreakdownByCategory(Long walletId);
}
