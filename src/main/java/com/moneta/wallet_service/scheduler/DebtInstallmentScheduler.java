package com.moneta.wallet_service.scheduler;

import com.moneta.wallet_service.service.DebtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebtInstallmentScheduler {

    private final DebtService debtService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void processMonthlyInstallments() {
        log.info("Aylık otomatik taksit senkronizasyonu başlıyor.");
        debtService.syncAllDueInstallments();
        log.info("Aylık otomatik taksit senkronizasyonu tamamlandı.");
    }
}