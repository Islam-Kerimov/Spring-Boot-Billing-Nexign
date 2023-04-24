package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"callType", "startTime", "endTime", "duration", "cost"})
public class BillingReportDto {

	private String callType;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private LocalTime duration;

	private Double cost;
}
