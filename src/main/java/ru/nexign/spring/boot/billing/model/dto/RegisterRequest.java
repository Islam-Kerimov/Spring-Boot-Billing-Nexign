package ru.nexign.spring.boot.billing.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "The username is required and not null")
    private String username;
    @NotBlank(message = "The password is required and not null")
    private String password;
}
