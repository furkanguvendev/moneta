package com.moneta.wallet_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Kullanıcı adı boş olamaz.")
        @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalıdır.")
        String userName,

        @NotBlank(message = "E-posta adresi boş olamaz.")
        @Email(message = "Geçerli bir e-posta adresi giriniz.")
        String email,

        @NotBlank(message = "Şifre boş olamaz.")
        @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır.")
        String password,

        String firstName,

        String lastName,

        @NotNull(message = "Bütçe başlangıç günü belirtilmelidir.")
        @Min(value = 1, message = "Bütçe başlangıç günü en az 1 olabilir.")
        @Max(value = 31, message = "Bütçe başlangıç günü en fazla 31 olabilir.")
        Integer budgetStartDay
) {}