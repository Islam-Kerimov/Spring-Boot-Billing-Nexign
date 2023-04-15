package ru.nexign.spring.boot.billing.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberDto {
    private String phoneNumber;
    private Integer balance;
}
