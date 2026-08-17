package com.moneta.wallet_service.mapper;

import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.dto.response.SimulationResponse;
import com.moneta.wallet_service.entity.InvestmentSimulation;
import com.moneta.wallet_service.enums.SimulationStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
public class SimulationMapper {

    public InvestmentSimulation toEntity(SimulationRequest request, Long userId) {
        if (request == null) return null;

        InvestmentSimulation simulation = new InvestmentSimulation();
        simulation.setUserId(userId);
        simulation.setWalletId(request.walletId());
        simulation.setAmount(request.amount());
        simulation.setInvestmentType(request.investmentType());
        simulation.setMaturityType(request.maturityType());
        simulation.setEntryValue(request.entryValue());
        simulation.setStartDate(LocalDate.now());
        simulation.setStatus(SimulationStatus.ACTIVE);
        return simulation;
    }

    public SimulationResponse toResponse(InvestmentSimulation entity, BigDecimal currentOrExitValue) {
        if (entity == null) return null;

        BigDecimal profitOrLossAmount = BigDecimal.ZERO;
        Double profitOrLossPercentage = 0.0;

        if (currentOrExitValue != null && entity.getEntryValue() != null && entity.getEntryValue().compareTo(BigDecimal.ZERO) > 0) {
            profitOrLossAmount = currentOrExitValue.subtract(entity.getEntryValue());
            profitOrLossPercentage = profitOrLossAmount
                    .divide(entity.getEntryValue(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return new SimulationResponse(
                entity.getId(),
                entity.getWalletId(),
                entity.getAmount(),
                entity.getInvestmentType(),
                entity.getMaturityType(),
                entity.getEntryValue(),
                currentOrExitValue,
                profitOrLossAmount,
                profitOrLossPercentage,
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getNotes()
        );
    }
}