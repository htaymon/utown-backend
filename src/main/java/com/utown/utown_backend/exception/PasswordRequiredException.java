package com.utown.utown_backend.exception;

public class PasswordRequiredException extends RuntimeException {
  public PasswordRequiredException(String message) {
    super(message);
  }
}
