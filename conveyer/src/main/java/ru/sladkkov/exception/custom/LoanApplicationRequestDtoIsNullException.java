package ru.sladkkov.exception.custom;

public class LoanApplicationRequestDtoIsNullException extends RuntimeException {

  public LoanApplicationRequestDtoIsNullException(String message, Throwable cause) {
    super(message, cause);
  }
}
