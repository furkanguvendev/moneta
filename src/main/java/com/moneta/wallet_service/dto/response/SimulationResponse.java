package com.moneta.wallet_service.dto.response;

import com.moneta.wallet_service.enums.InvestmentType;
import com.moneta.wallet_service.enums.MaturityType;
import com.moneta.wallet_service.enums.SimulationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulationResponse(
        Long id,
        Long walletId,
        BigDecimal amount,
        InvestmentType investmentType,
        MaturityType maturityType,
        BigDecimal entryValue,
        BigDecimal currentOrExitValue,
        BigDecimal profitOrLossAmount,
        Double profitOrLossPercentage,
        LocalDate startDate,
        LocalDate endDate,
        SimulationStatus status,
        String notes
) {}