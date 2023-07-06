package ru.pobopo.gymanager.services.gateway.exception;

public class MissingTokenException extends RuntimeException {

    public MissingTokenException() {
    }

    public MissingTokenException(String message) {
        super(message);
    }
}
