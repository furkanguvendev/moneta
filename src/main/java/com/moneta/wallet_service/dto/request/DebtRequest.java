package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.DebtType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DebtRequest(
        @NotBlank(message = "Kredi/Taksit başlığı boş olamaz.")
        String title,

        @NotNull(message = "Toplam kredi tutarı girilmelidir.")
        @Positive(message = "Tutar pozitif olmalıdır.")
        BigDecimal totalAmount,

        @NotNull(message = "Kredi tipi seçilmelidir.")
        DebtType debtType,

        @NotNull(message = "Taksit sayısı boş olamaz.")
        @Min(value = 1, message = "Taksit sayısı en az 1 olmalıdır.")
        Integer totalInstallments,

        LocalDate dueDate
) {}