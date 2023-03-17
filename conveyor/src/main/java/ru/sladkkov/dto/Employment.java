package ru.sladkkov.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sladkkov.enums.EmploymentStatus;
import ru.sladkkov.enums.PositionAtWork;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employment {

  private EmploymentStatus employmentStatus;

  @NotEmpty
  @Pattern(regexp = "\\d+", message = "ИНН состоит только из цифр")
  @Size(max = 12, message = "Количество цифр ИНН не может быть больше 12")
  private String employerINN;

  private PositionAtWork position;

  @NotNull private BigDecimal salary;

  @NotNull
  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer workExperienceTotal;

  @NotNull
  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer workExperienceCurrent;
}
