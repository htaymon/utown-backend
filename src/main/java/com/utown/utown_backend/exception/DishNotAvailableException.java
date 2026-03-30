package com.utown.utown_backend.exception;

public class DishNotAvailableException extends RuntimeException {
    public DishNotAvailableException(String message) {
        super(message);
    }
}
