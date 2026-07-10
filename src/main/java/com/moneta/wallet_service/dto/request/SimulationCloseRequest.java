package com.moneta.wallet_service.dto.request;

import java.math.BigDecimal;

public record SimulationCloseRequest(
        BigDecimal currentEvValue
) {}