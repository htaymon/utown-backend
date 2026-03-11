package com.utown.utown_backend.exception;

public class RestaurantClosedException extends RuntimeException {
    public RestaurantClosedException(String message) {
        super(message);
    }
}
