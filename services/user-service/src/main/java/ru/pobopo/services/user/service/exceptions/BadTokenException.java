package ru.pobopo.services.user.service.exceptions;

public class BadTokenException extends Exception {

    public BadTokenException() {
    }

    public BadTokenException(String message) {
        super(message);
    }
}
