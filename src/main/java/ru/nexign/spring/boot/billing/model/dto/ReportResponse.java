package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "phoneNumber", "tariffUuid", "operator", "payload", "totalCost", "monetaryUnit"})
public class ReportResponse {
	private Integer id;
	private String phoneNumber;
	@JsonProperty("tariff_id")
	private String tariffUuid;
	private String operator;
	private List<BillingReportDto> payload;
	private Double totalCost;
	private String monetaryUnit;
}
