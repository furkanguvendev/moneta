package com.moneta.wallet_service.dto.response;

public record CategoryResponse(
        Long id,
        String name,
        boolean isMandatory,
        boolean isDefault
) {}
