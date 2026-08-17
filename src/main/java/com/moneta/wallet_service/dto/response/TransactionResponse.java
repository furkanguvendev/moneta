package com.moneta.wallet_service.dto.response;

import com.moneta.wallet_service.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        Long walletId,
        String walletName,
        Long categoryId,
        String categoryName,
        boolean isMandatoryCategory,
        TransactionType transactionType,
        LocalDateTime transactionDate,

        String installmentGroupKey,
        Integer currentInstallment,
        Integer totalInstallment
) {}