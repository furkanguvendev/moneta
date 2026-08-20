package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionUpdateRequest(
        @NotNull(message = "İşlem tutarı boş olamaz.")
        @Positive(message = "Tutar pozitif bir değer olmalıdır.")
        BigDecimal amount,

        String description,

        @NotNull(message = "Kategori seçilmelidir.")
        Long categoryId,

        @NotNull(message = "İşlem tipi seçilmelidir.")
        TransactionType transactionType,

        LocalDateTime transactionDate
) {}