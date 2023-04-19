package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayResponse {
    private Integer id;
    private String phoneNumber;
    @JsonProperty("money")
    private Double balance;
}
