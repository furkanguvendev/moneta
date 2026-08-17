package com.moneta.wallet_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Kategori adı boş olamaz.")
        String name,

        boolean isMandatory
) {}