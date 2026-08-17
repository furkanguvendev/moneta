package com.moneta.wallet_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DebtPaymentRequest(
        @NotNull(message = "Ödemenin yapılacağı cüzdan ID'si boş olamaz")
        Long walletId,

        @NotNull(message = "Ödeme tutarı boş olamaz")
        @Positive(message = "Ödeme tutarı sıfırdan büyük olmalıdır")
        BigDecimal amount
) {}