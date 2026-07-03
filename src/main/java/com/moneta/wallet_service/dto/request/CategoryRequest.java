package com.moneta.wallet_service.dto.request;

public record CategoryRequest(
        String name,
        boolean isMandatory
) {}
