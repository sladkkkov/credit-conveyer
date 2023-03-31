package ru.sladkkov.exception;

public class ApplicationNotFoundException extends RuntimeException {
  public ApplicationNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
