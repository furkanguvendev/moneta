package com.moneta.wallet_service.enums;

import lombok.Getter;

@Getter
public enum DebtType {
    BORC("Borç (Ödenecek)"),
    ALACAK("Alacak (Tahsil Edilecek)");

    private final String description;

    DebtType(String description) {
        this.description = description;
    }
}