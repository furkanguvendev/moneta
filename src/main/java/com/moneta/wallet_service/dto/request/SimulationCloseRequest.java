package com.moneta.wallet_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SimulationCloseRequest(
        @NotNull(message = "Güncel varlık değeri boş bırakılamaz.")
        @Positive(message = "Güncel varlık değeri pozitif olmalıdır.")
        BigDecimal currentEvValue
) {}