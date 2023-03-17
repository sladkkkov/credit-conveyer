package ru.sladkkov.exception;

public class LoanApplicationRequestDtoIsNullException extends RuntimeException {

  public LoanApplicationRequestDtoIsNullException(String message, Throwable cause) {
    super(message, cause);
  }
}
