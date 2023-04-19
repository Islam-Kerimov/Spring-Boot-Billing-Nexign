package ru.nexign.spring.boot.billing.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "phoneNumber", "tariff_id", "balance", "operator"})
public class SubscriberDto {
    private Integer id;
    private String phoneNumber;
    @JsonProperty("tariff_id")
    private String tariffUuid;
    private Double balance;
    private String operator;
}
