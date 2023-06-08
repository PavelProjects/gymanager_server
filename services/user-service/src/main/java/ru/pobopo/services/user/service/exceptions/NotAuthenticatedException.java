package ru.pobopo.services.user.service.exceptions;

public class NotAuthenticatedException extends Exception {
    public NotAuthenticatedException(){}

    public NotAuthenticatedException(String message) {
        super(message);
    }
}
