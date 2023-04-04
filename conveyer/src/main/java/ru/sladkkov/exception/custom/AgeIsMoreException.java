package ru.sladkkov.exception.custom;

public class AgeIsMoreException extends RuntimeException {

  public AgeIsMoreException(String message, Throwable cause) {
    super(message, cause);
  }
}
