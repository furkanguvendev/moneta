package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.response.MonthlyBreakdownResponse;
import com.moneta.wallet_service.dto.response.MonthlySummaryResponse;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.AnalyticsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;

    public AnalyticsServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public MonthlySummaryResponse getMonthlySummary(Long userId) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        BigDecimal income = transactionRepository.findTotalIncomeByUserIdAndMonthAndYear(userId, currentMonth, currentYear);
        BigDecimal expense = transactionRepository.findTotalExpenseByUserIdAndMonthAndYear(userId, currentMonth, currentYear);

        return new MonthlySummaryResponse(
                income != null ? income : BigDecimal.ZERO,
                expense != null ? expense : BigDecimal.ZERO
        );
    }

    @Override
    public MonthlySummaryResponse getWalletMonthlySummary(Long walletId) {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        BigDecimal totalIncome = transactionRepository.findMonthlyTotalByWalletIdAndType(
                walletId, TransactionType.INCOME, currentMonth, currentYear
        );

        BigDecimal totalExpense = transactionRepository.findMonthlyTotalByWalletIdAndType(
                walletId, TransactionType.EXPENSE, currentMonth, currentYear
        );

        return new MonthlySummaryResponse(totalIncome, totalExpense);
    }

    @Override
    public List<MonthlyBreakdownResponse> getWalletMonthlyBreakdown(Long walletId) {
        return transactionRepository.findMonthlyBreakdownByWalletId(walletId);
    }
}