package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.response.MonthlySummaryResponse;

public interface AnalyticsService {
    MonthlySummaryResponse getMonthlySummary(Long userId);
    MonthlySummaryResponse getWalletMonthlySummary(Long walletId);
}