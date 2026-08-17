package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Debt;
import com.moneta.wallet_service.enums.DebtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    List<Debt> findByUserId(Long userId);

    List<Debt> findByUserIdAndDebtType(Long userId, DebtType debtType);

    List<Debt> findByUserIdAndIsCompletedFalse(Long userId);
}