package ru.nexign.spring.boot.billing.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
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

	@Null(groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "id must be null")
	private Integer id;

	@NotNull(groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "The phone number is required.")
	@Pattern(regexp = "^7\\d{10}$", message = "enter a valid phone number")
	private String phoneNumber;

	@JsonProperty("tariff_id")
	@NotBlank(groups = {Marker.OnUpdate.class, Marker.OnCreate.class}, message = "The tariff_id is required and not null")
	private String tariffUuid;

	@Null(groups = Marker.OnUpdate.class, message = "balance must be null")
	@NotNull(groups = Marker.OnCreate.class, message = "balance must not be null")
	@PositiveOrZero(groups = Marker.OnCreate.class, message = "must be greater or equals than 0")
	private Double balance;

	@Null(groups = Marker.OnUpdate.class, message = "operator must be null")
	@NotBlank(groups = Marker.OnCreate.class, message = "operator must not be null")
	private String operator;
}
