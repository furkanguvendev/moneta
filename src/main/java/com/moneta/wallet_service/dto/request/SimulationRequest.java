package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.InvestmentType;
import com.moneta.wallet_service.enums.MaturityType;
import java.math.BigDecimal;

public record SimulationRequest(
        Long walletId,
        BigDecimal amount,
        InvestmentType investmentType,
        MaturityType maturityType,
        BigDecimal entryValue
) {}