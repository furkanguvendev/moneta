package com.moneta.wallet_service.enums;

import lombok.Getter;

@Getter
public enum InvestmentType {
    FAIZ("Mevduat / Faiz"),
    DOLAR("Döviz / Dolar"),
    ALTIN("Altın"),
    BORSA("Borsa / Hisse Senedi");

    private final String displayName;

    InvestmentType(String displayName) {
        this.displayName = displayName;
    }
}