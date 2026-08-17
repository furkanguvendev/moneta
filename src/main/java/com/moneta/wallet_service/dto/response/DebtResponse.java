package com.moneta.wallet_service.dto.response;

import com.moneta.wallet_service.enums.DebtType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtResponse(
        Long id,
        String title,
        BigDecimal totalAmount,
        BigDecimal remainingAmount,
        DebtType debtType,
        LocalDate dueDate,
        boolean isCompleted
) {}