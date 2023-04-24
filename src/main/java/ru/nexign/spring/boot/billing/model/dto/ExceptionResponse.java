package ru.nexign.spring.boot.billing.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {

	@JsonProperty("error_status")
	private final String errorStatus;

	private final String message;
}
