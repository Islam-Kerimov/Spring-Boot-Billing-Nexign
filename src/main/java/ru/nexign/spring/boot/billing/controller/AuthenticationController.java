package ru.nexign.spring.boot.billing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nexign.spring.boot.billing.model.dto.AuthenticationRequest;
import ru.nexign.spring.boot.billing.model.dto.AuthenticationResponse;
import ru.nexign.spring.boot.billing.model.dto.RegisterRequest;
import ru.nexign.spring.boot.billing.security.AuthenticationService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "The Authentications API. Contains an operation to get an existing user's token.")
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/register")
	@Operation(summary = "Авторизация абонента в БД",
		description = "Абонент авторизовывается в БД для получение токена")
	public ResponseEntity<AuthenticationResponse> register(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "В теле запроса обязательно должен быть имя абонента и пароль, по которым абонент будет проходить аутентификацию",
			required = true)
		@RequestBody RegisterRequest request) {
		return ok(service.register(request));
	}

	@PostMapping("/authenticate")
	@Operation(summary = "Аутентификация абонента",
		description = "Абонент проходит аутентификацию для получение токена")
	public ResponseEntity<AuthenticationResponse> authenticate(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "В теле запроса обязательно должен быть имя абонента и пароль, по которым абонент будет проходить аутентификацию",
			required = true)
		@RequestBody AuthenticationRequest request) {
		return ok(service.authenticate(request));
	}
}
