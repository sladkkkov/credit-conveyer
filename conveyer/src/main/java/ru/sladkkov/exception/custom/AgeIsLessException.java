package ru.sladkkov.exception.custom;

public class AgeIsLessException extends RuntimeException {
  public AgeIsLessException(String message, Throwable cause) {
    super(message, cause);
  }
}
