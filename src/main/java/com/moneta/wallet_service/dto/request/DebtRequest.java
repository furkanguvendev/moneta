package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.DebtType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtRequest(
        @NotBlank(message = "Borç/Alacak başlığı boş olamaz.")
        String title,

        @NotNull(message = "Toplam tutar girilmelidir.")
        @Positive(message = "Tutar pozitif olmalıdır.")
        BigDecimal totalAmount,

        @NotNull(message = "Borç tipi (BORC/ALACAK) seçilmelidir.")
        DebtType debtType,

        LocalDate dueDate
) {}