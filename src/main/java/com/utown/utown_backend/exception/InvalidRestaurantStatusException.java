package com.utown.utown_backend.exception;

public class InvalidRestaurantStatusException extends RuntimeException {
    public InvalidRestaurantStatusException(String message) {
        super(message);
    }
}
