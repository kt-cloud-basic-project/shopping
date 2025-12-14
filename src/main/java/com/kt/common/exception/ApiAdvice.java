package com.kt.common.exception;

import java.util.Arrays;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kt.common.response.ErrorResponse;
import com.kt.common.support.Message;
import com.kt.common.support.MessageType;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiAdvice {
	private final ApplicationEventPublisher eventPublisher;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse.ErrorData> internalServerError(Exception e) {
		e.printStackTrace();

		eventPublisher.publishEvent(
			Message.error(
				MessageType.ERROR,
				"Internal Server Error",
				e.getMessage()
			)
		);

		return ErrorResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버에러입니다. 백엔드팀에 문의하세요.");
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse.ErrorData> customException(CustomException e) {
		return ErrorResponse.error(e.getErrorCode().getStatus(), e.getErrorCode().getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse.ErrorData> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		e.printStackTrace();
		var details = Arrays.toString(e.getDetailMessageArguments());
		var message = details.split(",", 2)[1].replace("]", "").trim();

		return ErrorResponse.error(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse.ErrorData> authorizationDeniedException(AuthorizationDeniedException e) {
		return ErrorResponse.error(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse.ErrorData> authenticationException(AuthenticationException e) {
		return ErrorResponse.error(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.");
	}

}
