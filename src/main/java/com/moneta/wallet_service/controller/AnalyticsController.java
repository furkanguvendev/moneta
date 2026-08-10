package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.response.MonthlySummaryResponse;
import com.moneta.wallet_service.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/user/{userId}/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(@PathVariable Long userId) {
        return ResponseEntity.ok(analyticsService.getMonthlySummary(userId));
    }

    @GetMapping("/wallet/{walletId}/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getWalletMonthlySummary(@PathVariable Long walletId) {
        return ResponseEntity.ok(analyticsService.getWalletMonthlySummary(walletId));
    }
}