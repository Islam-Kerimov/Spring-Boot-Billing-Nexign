package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.PositiveOrZero;
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

	@Null(groups = Marker.OnCreate.class, message = "id must be null")
	private Integer id;

	@NotBlank(groups = Marker.OnCreate.class, message = "The uuid is required and not null")
	private String uuid;

	@NotBlank(groups = Marker.OnCreate.class, message = "The name is required and not null")
	private String name;

	@PositiveOrZero(groups = Marker.OnCreate.class, message = "The fixMin must be null OR greater or equals than 0")
	private Integer fixMin;

	@PositiveOrZero(groups = Marker.OnCreate.class, message = "The fixPrice must be null OR greater or equals than 0")
	private Double fixPrice;

	@PositiveOrZero(groups = Marker.OnCreate.class, message = "The firstMin must be null OR greater or equals than 0")
	private Integer firstMin;

	@PositiveOrZero(groups = Marker.OnCreate.class, message = "The firstPrice must be null OR greater or equals than 0")
	private Double firstPrice;

	@PositiveOrZero(groups = Marker.OnCreate.class, message = "The minutePrice must be null OR greater or equals than 0")
	private Double minutePrice;

	private Boolean incomingInside;

	private Boolean outgoingInside;

	private Boolean incomingAnother;

	private Boolean outgoingAnother;

	@NotNull(groups = Marker.OnCreate.class, message = "The monetaryUnit must be not null")
	private String monetaryUnit = "rubles";

	private String redirect;

	@NotBlank(groups = Marker.OnCreate.class, message = "The operator is required and not null")
	private String operator;
}
