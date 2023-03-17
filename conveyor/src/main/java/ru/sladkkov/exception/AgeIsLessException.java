package ru.sladkkov.exception;

public class AgeIsLessException extends RuntimeException {
  public AgeIsLessException(String message, Throwable cause) {
    super(message, cause);
  }
}
