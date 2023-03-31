package ru.sladkkov.exception.custom;

public class WorkExperienceNotEnoughException extends RuntimeException {
  public WorkExperienceNotEnoughException(String message, Throwable cause) {
    super(message, cause);
  }
}
