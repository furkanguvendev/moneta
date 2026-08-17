package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record WalletRequest(
        @NotBlank(message = "Cüzdan adı boş olamaz.")
        String name,

        @NotNull(message = "Bakiye alanı boş bırakılamaz.")
        @PositiveOrZero(message = "Bakiye 0 veya pozitif bir değer olmalıdır.")
        BigDecimal balance,

        @NotNull(message = "Para birimi seçilmelidir.")
        Currency currency
) {}