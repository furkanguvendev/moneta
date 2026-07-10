package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.SimulationCloseRequest;
import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.entity.InvestmentSimulation;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.InvestmentType;
import com.moneta.wallet_service.enums.SimulationStatus;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.InvestmentSimulationRepository;
import com.moneta.wallet_service.service.InvestmentSimulationService;
import com.moneta.wallet_service.service.TransactionService;
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
    private final TransactionService transactionService;
    private final CategoryRepository categoryRepository;

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
        if (passedDays <= 0) passedDays = 1;

        BigDecimal profitOrLoss = BigDecimal.ZERO;

        if (simulation.getInvestmentType() == InvestmentType.FAIZ) {
            BigDecimal annualRate = simulation.getEntryValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal daysRatio = BigDecimal.valueOf(passedDays).divide(BigDecimal.valueOf(365), 4, RoundingMode.HALF_UP);
            profitOrLoss = simulation.getAmount().multiply(annualRate).multiply(daysRatio);
        } else {
            BigDecimal count = simulation.getAmount().divide(simulation.getEntryValue(), 6, RoundingMode.HALF_UP);
            BigDecimal currentTotal = count.multiply(request.currentEvValue());
            profitOrLoss = currentTotal.subtract(simulation.getAmount());
        }

        if (profitOrLoss.compareTo(BigDecimal.ZERO) != 0) {
            String description = String.format("%s Yatırım Simülasyonu Kapanış Raporu (%d Gün)",
                    simulation.getInvestmentType().name(), passedDays);

            TransactionType txType = profitOrLoss.compareTo(BigDecimal.ZERO) > 0
                    ? TransactionType.INCOME
                    : TransactionType.EXPENSE;

            BigDecimal finalAmount = profitOrLoss.abs().setScale(2, RoundingMode.HALF_UP);

            Category investmentCategory = categoryRepository.findAll().stream()
                    .filter(c -> "Yatırım".equalsIgnoreCase(c.getName()) && c.getUser() == null)
                    .findFirst()
                    .orElseGet(() -> {
                        Category newCat = new Category();
                        newCat.setName("Yatırım");
                        newCat.setMandatory(false);
                        newCat.setDefault(true);
                        return categoryRepository.save(newCat);
                    });

            TransactionRequest txRequest = new TransactionRequest(
                    finalAmount,
                    description,
                    simulation.getWalletId(),
                    investmentCategory.getId(),
                    txType.name()
            );

            transactionService.addTransaction(txRequest);
        }

        if (now.isAfter(simulation.getEndDate()) || now.isEqual(simulation.getEndDate())) {
            simulation.setStatus(SimulationStatus.COMPLETED);
        } else {
            simulation.setStatus(SimulationStatus.CANCELLED);
        }

        return simulationRepository.save(simulation);
    }
}