package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.InvestmentType;
import com.moneta.wallet_service.enums.MaturityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SimulationRequest(
        @NotNull(message = "Cüzdan ID boş olamaz.")
        Long walletId,

        @NotNull(message = "Tutar girilmelidir.")
        @Positive(message = "Tutar pozitif bir değer olmalıdır.")
        BigDecimal amount,

        @NotNull(message = "Yatırım türü seçilmelidir.")
        InvestmentType investmentType,

        MaturityType maturityType,

        @NotNull(message = "Giriş varlık değeri girilmelidir.")
        @Positive(message = "Giriş değeri pozitif olmalıdır.")
        BigDecimal entryValue
) {}