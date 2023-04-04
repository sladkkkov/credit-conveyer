package ru.sladkkov.dto;

import java.time.LocalDate;
import lombok.Data;
import ru.sladkkov.enums.Gender;
import ru.sladkkov.enums.MartialStatus;

@Data
public class FinishRegistrationRequestDto {

  private Gender gender;
  private MartialStatus martialStatus;
  private Integer dependentAmount;
  private LocalDate passportIssueDate;
  private String passportIssueBranch;
  private EmploymentDto employment;
  private String account;


}

