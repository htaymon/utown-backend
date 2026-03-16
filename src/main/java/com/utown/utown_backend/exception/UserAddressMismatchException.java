package com.utown.utown_backend.exception;

public class UserAddressMismatchException extends RuntimeException {
    public UserAddressMismatchException(String message) {
        super(message);
    }
}
