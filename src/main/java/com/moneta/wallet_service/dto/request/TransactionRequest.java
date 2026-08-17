package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "İşlem tutarı boş olamaz.")
        @Positive(message = "Tutar pozitif bir değer olmalıdır.")
        BigDecimal amount,

        String description,

        @NotNull(message = "Cüzdan seçilmelidir.")
        Long walletId,

        @NotNull(message = "Kategori seçilmelidir.")
        Long categoryId,

        @NotNull(message = "İşlem tipi seçilmelidir.")
        TransactionType transactionType,

        // Taksitli harcama ise taksit sayısı (Varsayılan 1)
        @Min(value = 1, message = "Taksit sayısı en az 1 olmalıdır.")
        Integer totalInstallment
) {}