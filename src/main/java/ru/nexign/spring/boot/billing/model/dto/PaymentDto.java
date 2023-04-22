package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    @Null(groups = Marker.OnUpdate.class, message = "id must be null")
    private Integer id;
    @NotNull(groups = Marker.OnUpdate.class, message = "The phone number is required.")
    @Pattern(regexp = "^7\\d{10}$", message = "enter a valid phone number")
    private String phoneNumber;

    @NotNull(groups = Marker.OnUpdate.class, message = "The money is required.")
    @Min(value = 1, message = "must be greater or equals than 1")
    private Double money;
}
