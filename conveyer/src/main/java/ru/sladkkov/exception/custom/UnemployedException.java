package ru.sladkkov.exception.custom;

public class UnemployedException extends RuntimeException {

  public UnemployedException(String message, Throwable cause) {
    super(message, cause);
  }
}
