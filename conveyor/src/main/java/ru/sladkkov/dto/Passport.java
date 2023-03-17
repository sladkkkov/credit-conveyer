package ru.sladkkov.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passport {

  @NotEmpty(message = "Серия паспорта не должна быть пустым")
  @Size(min = 4, max = 4)
  @Pattern(regexp = "\\d{4}", message = "Серия паспорта состоит только из цифр")
  private String passportSeries;

  @NotEmpty(message = "Номер паспорта не должен быть пустым")
  @Size(min = 6, max = 6)
  @Pattern(regexp = "\\d{6}", message = "Номер паспорта состоит только из цифр")
  private String passportNumber;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @NotNull
  private LocalDate passportIssueDate;

  @NotEmpty
  @Size(min = 5, max = 50, message = "Орган выдавший паспорт должен содержать от 5 до 50 символов")
  private String passportIssueBranch;
}
