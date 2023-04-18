package ru.nexign.spring.boot.billing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewTariffRequest {
	private String uuid;
	private String name;
	private Integer fixMin;
	private Double fixPrice;
	private Integer firstMin;
	private Double firstPrice;
	private Double minutePrice;
	private Boolean incomingInside;
	private Boolean outgoingInside;
	private Boolean incomingAnother;
	private Boolean outgoingAnother;
	private String monetaryUnit = "rubles";
	private String redirect;
	private String operator;
}
