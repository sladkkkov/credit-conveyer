package ru.sladkkov.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sladkkov.enums.Gender;
import ru.sladkkov.enums.MartialStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringData {

  @NotNull(message = "LoanApplicationRequest не должно равняться null")
  @Valid
  private LoanApplicationRequest loanApplicationRequest;

  private Gender gender;

  private MartialStatus maritalStatus;

  @NotNull
  @Min(value = 0, message = "Количество иждивенцев не должно быть меньше 0")
  private Integer dependentAmount;

  @Valid private Employment employment;

  @Size(min = 20, max = 20, message = "Номер счета должен состоять из 20 цифр")
  @Pattern(regexp = "\\d+", message = "Номер счета состоит только из цифр")
  private String account;

  private Boolean isInsuranceEnabled;

  private Boolean isSalaryClient;

  private @Valid Passport passport;
}
