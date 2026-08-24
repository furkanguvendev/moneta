package com.moneta.wallet_service.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtPaymentRequest(
        @NotNull(message = "Ödemenin yapılacağı cüzdan ID'si boş olamaz.")
        Long walletId,

        BigDecimal amount,

        Long categoryId,

        LocalDate transactionDate
) {}