package ru.sladkkov.validator;

import jakarta.validation.ConstraintValidator;import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidation implements ConstraintValidator<CheckBirthday, LocalDate> {
  @Override
  public void initialize(CheckBirthday constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(
      LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
    return true;

    //TODO
    // Period.between(localDate, LocalDate.now()).getYears() >= 18;
  }
}
