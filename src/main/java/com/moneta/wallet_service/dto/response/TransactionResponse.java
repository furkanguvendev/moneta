package com.moneta.wallet_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        String categoryName,
        boolean isMandatory,
        String walletName,
        String transactionType,
        LocalDateTime transactionDate
) {}
