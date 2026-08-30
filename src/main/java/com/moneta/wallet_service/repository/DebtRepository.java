package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Debt;
import com.moneta.wallet_service.enums.DebtType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> findByUserId(Long userId);

    List<Debt> findByUserIdAndDebtType(Long userId, DebtType debtType);

    List<Debt> findByUserIdAndIsCompletedFalse(Long userId);

    List<Debt> findByIsCompletedFalseAndDebtType(DebtType debtType);

    List<Debt> findByWalletIdAndIsCompletedFalseAndDebtType(Long walletId, DebtType debtType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Debt d where d.id = :id")
    Optional<Debt> findByIdForUpdate(@Param("id") Long id);
}