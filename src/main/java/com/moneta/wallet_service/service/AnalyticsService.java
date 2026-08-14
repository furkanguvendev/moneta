package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.response.MonthlyBreakdownResponse;
import com.moneta.wallet_service.dto.response.MonthlySummaryResponse;

import java.util.List;

public interface AnalyticsService {
    MonthlySummaryResponse getMonthlySummary(Long userId);
    MonthlySummaryResponse getWalletMonthlySummary(Long walletId);
    List<MonthlyBreakdownResponse> getWalletMonthlyBreakdown(Long walletId);
}