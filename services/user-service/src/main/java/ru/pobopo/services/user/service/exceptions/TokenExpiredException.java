package ru.pobopo.services.user.service.exceptions;

public class TokenExpiredException extends Exception {

    public TokenExpiredException() {
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
