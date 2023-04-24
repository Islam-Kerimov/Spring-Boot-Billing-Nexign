package ru.nexign.spring.boot.billing.handler;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.nexign.spring.boot.billing.model.dto.ExceptionResponse;
import ru.nexign.spring.boot.billing.model.dto.ValidationErrorResponse;
import ru.nexign.spring.boot.billing.model.dto.Violation;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Обработчик выбрасываемых ошибок.
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(BAD_REQUEST)
	public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
		final List<Violation> violations = e.getConstraintViolations().stream()
			.map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
			.toList();
		return new ValidationErrorResponse(violations);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(BAD_REQUEST)
	public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
			.map(error -> new Violation(error.getField(), error.getDefaultMessage()))
			.toList();
		return new ValidationErrorResponse(violations);
	}

	@ExceptionHandler({EntityNotFoundException.class})
	@ResponseStatus(NOT_FOUND)
	public ExceptionResponse onNotFoundException(EntityNotFoundException e) {
		return new ExceptionResponse(NOT_FOUND.getReasonPhrase(), e.getMessage());
	}

	@ExceptionHandler({EntityExistsException.class, RuntimeException.class})
	@ResponseStatus(BAD_REQUEST)
	public ExceptionResponse onExistException(RuntimeException e) {
		return new ExceptionResponse(BAD_REQUEST.getReasonPhrase(), e.getMessage());
	}
}
