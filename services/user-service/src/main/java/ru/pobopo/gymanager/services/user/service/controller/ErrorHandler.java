package ru.pobopo.gymanager.services.user.service.controller;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.pobopo.gymanager.services.user.service.context.RequestContextHolder;
import ru.pobopo.gymanager.services.user.service.exception.AccessDeniedException;
import ru.pobopo.gymanager.services.user.service.exception.BadRequestException;
import ru.pobopo.gymanager.services.user.service.exception.TokenExpiredException;
import ru.pobopo.gymanager.shared.objects.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final String LOG_EXCEPTION_TEMPLATE = "[{}] Exception {} - {}";

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse accessDenied(AccessDeniedException exception) {
        logException(exception);
        return new ErrorResponse(
            exception.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(BadRequestException exception) {
        logException(exception);
        return new ErrorResponse(
            exception.getMessage()
        );
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse jwtException(JwtException exception) {
        logException(exception);
        return new ErrorResponse(
            exception.getMessage()
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse tokenExpired(TokenExpiredException exception) {
        logException(exception);
        return new ErrorResponse(
            exception.getMessage()
        );
    }
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse badCredits(BadCredentialsException exception) {
        logException(exception);
        return new ErrorResponse(
            "Wrong login/password"
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse runtimeException(RuntimeException exception) {
        logException(exception);
        return new ErrorResponse(
            "Something gone wrong :(",
            exception
        );
    }

    private static void logException(Throwable exception) {
        log.error(LOG_EXCEPTION_TEMPLATE, RequestContextHolder.getRequestUuidString(), exception.getClass(), exception.getMessage(), exception);
    }
}
