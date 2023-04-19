package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "uuid", "name", "fixMin", "fixPrice", "firstMin", "firstPrice", "minutePrice",
	"incomingInside", "outgoingInside", "incomingAnother", "outgoingAnother", "monetaryUnit", "redirect", "operator"})
public class TariffDto {
	private Integer id;
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
