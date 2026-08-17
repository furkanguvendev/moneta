package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.SimulationCloseRequest;
import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.dto.response.SimulationResponse;
import com.moneta.wallet_service.entity.InvestmentSimulation;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.InvestmentType;
import com.moneta.wallet_service.enums.SimulationStatus;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.mapper.SimulationMapper;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.InvestmentSimulationRepository;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.InvestmentSimulationService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestmentSimulationServiceImpl implements InvestmentSimulationService {

    private final InvestmentSimulationRepository simulationRepository;
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final SimulationMapper simulationMapper;

    @Override
    public List<SimulationResponse> getActiveSimulations(Long userId) {
        List<InvestmentSimulation> list = simulationRepository.findByUserIdAndStatus(userId, SimulationStatus.ACTIVE);
        return list.stream().map(sim -> simulationMapper.toResponse(sim, sim.getEntryValue())).toList();
    }

    @Override
    @Transactional
    public SimulationResponse createSimulation(Long userId, SimulationRequest request) {
        Wallet wallet = walletService.getWalletEntityById(request.walletId());

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Cüzdandaki bakiye simülasyon başlatmak için yetersiz!");
        }

        InvestmentSimulation simulation = simulationMapper.toEntity(request, userId);

        LocalDate now = LocalDate.now();
        if (request.investmentType() == InvestmentType.FAIZ && request.maturityType() != null) {
            switch (request.maturityType()) {
                case GUNLUK -> simulation.setEndDate(now.plusDays(1));
                case AYLIK -> simulation.setEndDate(now.plusMonths(1));
                case YILLIK -> simulation.setEndDate(now.plusYears(1));
            }
        } else {
            simulation.setEndDate(now.plusDays(30));
        }

        InvestmentSimulation savedSimulation = simulationRepository.save(simulation);

        walletService.updateBalance(request.walletId(), request.amount().negate());

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(request.amount());
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setDescription("Yatırım Simülasyonu Başlatıldı: " + request.investmentType().name());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setInvestmentSimulationId(savedSimulation.getId());

        categoryRepository.findById(1L).ifPresent(transaction::setCategory);
        transactionRepository.save(transaction);

        return simulationMapper.toResponse(savedSimulation, savedSimulation.getEntryValue());
    }

    @Override
    @Transactional
    public SimulationResponse closeOrCancelSimulation(Long id, Long userId, SimulationCloseRequest request) {
        InvestmentSimulation simulation = getSimulationEntityById(id);

        if (!simulation.getUserId().equals(userId)) {
            throw new RuntimeException("Bu simülasyon üzerinde işlem yapma yetkiniz yok!");
        }

        if (simulation.getStatus() != SimulationStatus.ACTIVE) {
            throw new RuntimeException("Sadece aktif durumdaki simülasyonlar kapatılabilir!");
        }

        LocalDate now = LocalDate.now();
        BigDecimal returnAmount = simulation.getAmount();

        if (simulation.getInvestmentType() == InvestmentType.FAIZ) {
            long passedDays = ChronoUnit.DAYS.between(simulation.getStartDate(), now);
            if (passedDays > 0) {
                BigDecimal annualRate = simulation.getEntryValue().divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
                BigDecimal daysRatio = BigDecimal.valueOf(passedDays).divide(BigDecimal.valueOf(365), 8, RoundingMode.HALF_UP);
                BigDecimal profit = simulation.getAmount().multiply(annualRate).multiply(daysRatio);
                returnAmount = simulation.getAmount().add(profit).setScale(2, RoundingMode.HALF_UP);
            }
        } else if (request.currentEvValue() != null && simulation.getEntryValue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal unitCount = simulation.getAmount().divide(simulation.getEntryValue(), 8, RoundingMode.HALF_UP);
            returnAmount = unitCount.multiply(request.currentEvValue()).setScale(2, RoundingMode.HALF_UP);
        }

        walletService.updateBalance(simulation.getWalletId(), returnAmount);

        Wallet wallet = walletService.getWalletEntityById(simulation.getWalletId());
        Transaction closeTx = new Transaction();
        closeTx.setWallet(wallet);
        closeTx.setAmount(returnAmount);
        closeTx.setTransactionType(TransactionType.INCOME);
        closeTx.setDescription("Yatırım Simülasyonu Kapatıldı: " + simulation.getInvestmentType().name());
        closeTx.setTransactionDate(LocalDateTime.now());
        closeTx.setInvestmentSimulationId(simulation.getId());

        categoryRepository.findById(1L).ifPresent(closeTx::setCategory);
        transactionRepository.save(closeTx);

        if (now.isAfter(simulation.getEndDate()) || now.isEqual(simulation.getEndDate())) {
            simulation.setStatus(SimulationStatus.COMPLETED);
        } else {
            simulation.setStatus(SimulationStatus.CANCELLED);
        }

        return simulationMapper.toResponse(simulationRepository.save(simulation), request.currentEvValue());
    }

    @Override
    public InvestmentSimulation getSimulationEntityById(Long id) {
        return simulationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Simülasyon bulunamadı! ID: " + id));
    }

    @Override
    @Transactional
    public void deleteSimulationById(Long id) {
        InvestmentSimulation simulation = simulationRepository.findById(id).orElse(null);

        if (simulation != null) {
            List<Transaction> transactions = transactionRepository.findByInvestmentSimulationId(id);
            BigDecimal netAdjustment = BigDecimal.ZERO;

            for (Transaction tx : transactions) {
                if (tx.getTransactionType() == TransactionType.EXPENSE) {
                    netAdjustment = netAdjustment.add(tx.getAmount());
                } else if (tx.getTransactionType() == TransactionType.INCOME) {
                    netAdjustment = netAdjustment.subtract(tx.getAmount());
                }
            }

            if (!transactions.isEmpty()) {
                transactionRepository.deleteAll(transactions);
            }

            if (netAdjustment.compareTo(BigDecimal.ZERO) != 0) {
                walletService.updateBalance(simulation.getWalletId(), netAdjustment);
            }

            simulationRepository.delete(simulation);
        }
    }
}