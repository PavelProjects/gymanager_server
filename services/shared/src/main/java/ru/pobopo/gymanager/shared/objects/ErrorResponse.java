package ru.pobopo.gymanager.shared.objects;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private Exception exception;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
    }
}
