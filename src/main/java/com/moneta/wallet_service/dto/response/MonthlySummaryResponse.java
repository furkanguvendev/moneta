package com.moneta.wallet_service.dto.response;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpense
) {}