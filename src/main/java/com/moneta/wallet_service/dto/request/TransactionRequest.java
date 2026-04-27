package com.moneta.wallet_service.dto.request;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        String description,
        Long walletId,
        Long categoryId,
        String transactionType
) {}
