package com.moneta.wallet_service.dto.request;

import com.moneta.wallet_service.enums.TransactionType;

public record CategoryRequest(
        String name,
        boolean isMandatory
) {}
