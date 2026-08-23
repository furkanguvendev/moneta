package com.moneta.wallet_service.enums;

import lombok.Getter;

@Getter
public enum DebtType {
    BIREYSEL_KREDI("Bireysel Kredi"),
    KONUT_KREDISI("Konut Kredisi"),
    TASIT_KREDISI("Taşıt Kredisi"),
    KREDI_KARTI_TAKSIDI("Kredi Kartı Taksidi"),
    DIGER("Diğer Kredi/Taksit");

    private final String description;

    DebtType(String description) {
        this.description = description;
    }
}