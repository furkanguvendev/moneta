package com.moneta.wallet_service.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UserUpdateRequest(
        String firstName,
        String lastName,

        @Min(value = 1, message = "Bütçe başlangıç günü en az 1 olmalıdır.")
        @Max(value = 28, message = "Bütçe başlangıç günü en fazla 28 olmalıdır.")
        Integer budgetStartDay
) {}