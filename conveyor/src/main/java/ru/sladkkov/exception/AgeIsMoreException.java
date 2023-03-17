package ru.sladkkov.exception;

public class AgeIsMoreException extends RuntimeException {

  public AgeIsMoreException(String message, Throwable cause) {
    super(message, cause);
  }
}
