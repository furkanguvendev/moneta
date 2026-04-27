package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.Currency;

import java.math.BigDecimal;

public record WalletRequest(
        String name,
        BigDecimal balance,
        Currency currency
) {}
