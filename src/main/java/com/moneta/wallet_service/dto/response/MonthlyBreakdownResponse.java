package com.moneta.wallet_service.dto.response;

import java.math.BigDecimal;

public record MonthlyBreakdownResponse(
        int year,
        int month,
        BigDecimal totalIncome,
        BigDecimal totalExpense
) {}