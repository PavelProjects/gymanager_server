package ru.pobopo.gymanager.services.user.service.exception;

public class TokenExpiredException extends Exception {

    public TokenExpiredException() {
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
