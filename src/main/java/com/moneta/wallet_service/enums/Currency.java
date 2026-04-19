package com.moneta.wallet_service.enums;

import lombok.Getter;

@Getter
public enum Currency {
    TRY("Türk Lirası", "₺"),
    USD("Amerikan Doları", "$"),
    EUR("Euro", "€"),
    GBP("İngiliz Sterlini", "£");

    private final String displayName;
    private final String symbol;

    Currency(String displayName, String symbol){
        this.displayName = displayName;
        this.symbol = symbol;
    }
}
