package ru.pobopo.services.user.service.exceptions;

public class AccessDeniedException extends Exception{

    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
