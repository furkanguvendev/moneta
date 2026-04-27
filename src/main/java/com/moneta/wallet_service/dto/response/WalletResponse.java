package com.moneta.wallet_service.dto.response;

import com.moneta.wallet_service.enums.Currency;

import java.math.BigDecimal;

public record WalletResponse(
        Long id,
        String name,
        BigDecimal balance,
        Currency currency,
        String ownerName
) {}
