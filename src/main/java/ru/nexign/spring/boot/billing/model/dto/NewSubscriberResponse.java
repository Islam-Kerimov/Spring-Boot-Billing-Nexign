package ru.nexign.spring.boot.billing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewSubscriberResponse {
    private Integer id;
    private String phoneNumber;
    private String tariffUuid;
    private Double balance;
    private String operator;
}
