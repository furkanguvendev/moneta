package com.moneta.wallet_service.dto.response;

import java.math.BigDecimal;

public record TransactionStatisticsResponse(
        Long categoryId,
        String categoryName,
        BigDecimal totalAmount,
        double percentage
) {}