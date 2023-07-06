package ru.pobopo.gymanager.services.user.service.exception;

public class AccessDeniedException extends Exception{

    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
