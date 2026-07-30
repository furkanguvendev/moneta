package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.SimulationCloseRequest;
import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.entity.InvestmentSimulation;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.InvestmentType;
import com.moneta.wallet_service.enums.SimulationStatus;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.repository.InvestmentSimulationRepository;
import com.moneta.wallet_service.repository.WalletRepository;
import com.moneta.wallet_service.service.InvestmentSimulationService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentSimulationServiceImpl implements InvestmentSimulationService {

    private final InvestmentSimulationRepository simulationRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    @Override
    public List<InvestmentSimulation> getActiveSimulations(Long userId) {
        return simulationRepository.findByUserIdAndStatus(userId, SimulationStatus.ACTIVE);
    }

    @Override
    @Transactional
    public InvestmentSimulation createSimulation(Long userId, SimulationRequest request) {
        Wallet wallet = walletService.getWalletEntityById(request.walletId());

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Cüzdanda simülasyon başlatacak kadar yeterli bakiye yok!");
        }

        // 1. Cüzdan Bakiyesini Güncelle
        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        walletRepository.saveAndFlush(wallet);

        // 2. Simülasyon Kaydını Oluştur
        InvestmentSimulation simulation = new InvestmentSimulation();
        simulation.setUserId(userId);
        simulation.setWalletId(request.walletId());
        simulation.setAmount(request.amount());
        simulation.setInvestmentType(request.investmentType());
        simulation.setEntryValue(request.entryValue());
        simulation.setStartDate(LocalDate.now());
        simulation.setEndDate(LocalDate.now().plusDays(30));
        simulation.setStatus(SimulationStatus.ACTIVE);

        return simulationRepository.save(simulation);
    }

    @Override
    @Transactional
    public InvestmentSimulation closeOrCancelSimulation(Long id, Long userId, SimulationCloseRequest request) {
        InvestmentSimulation simulation = simulationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Simülasyon bulunamadı! ID: " + id));

        if (!simulation.getUserId().equals(userId)) {
            throw new RuntimeException("Bu simülasyon üzerinde işlem yapma yetkiniz yok!");
        }

        if (simulation.getStatus() != SimulationStatus.ACTIVE) {
            throw new RuntimeException("Sadece aktif durumdaki simülasyonlar kapatılabilir!");
        }

        LocalDate now = LocalDate.now();
        long passedDays = ChronoUnit.DAYS.between(simulation.getStartDate(), now);

        BigDecimal profitOrLoss = BigDecimal.ZERO;

        // Aynı gün kapatmalarda getiri hesaplama (passedDays > 0 kuralı)
        if (passedDays > 0) {
            if (simulation.getInvestmentType() == InvestmentType.FAIZ) {
                BigDecimal annualRate = simulation.getEntryValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal daysRatio = BigDecimal.valueOf(passedDays).divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
                profitOrLoss = simulation.getAmount().multiply(annualRate).multiply(daysRatio);
            } else {
                BigDecimal count = simulation.getAmount().divide(simulation.getEntryValue(), 6, RoundingMode.HALF_UP);
                BigDecimal currentTotal = count.multiply(request.currentEvValue());
                profitOrLoss = currentTotal.subtract(simulation.getAmount());
            }
        }

        Wallet wallet = walletService.getWalletEntityById(simulation.getWalletId());
        BigDecimal returnAmount = simulation.getAmount().add(profitOrLoss);

        wallet.setBalance(wallet.getBalance().add(returnAmount));
        walletRepository.saveAndFlush(wallet);

        if (now.isAfter(simulation.getEndDate()) || now.isEqual(simulation.getEndDate())) {
            simulation.setStatus(SimulationStatus.COMPLETED);
        } else {
            simulation.setStatus(SimulationStatus.CANCELLED);
        }

        return simulationRepository.save(simulation);
    }
}