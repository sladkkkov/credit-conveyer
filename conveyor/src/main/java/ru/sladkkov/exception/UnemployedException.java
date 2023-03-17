package ru.sladkkov.exception;

public class UnemployedException extends RuntimeException {

  public UnemployedException(String message, Throwable cause) {
    super(message, cause);
  }
}
