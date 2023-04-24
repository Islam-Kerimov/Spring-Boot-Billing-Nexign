package ru.nexign.spring.boot.billing.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingRequest {

	@NotBlank(message = "action must be 'run'")
	private String action;
}
