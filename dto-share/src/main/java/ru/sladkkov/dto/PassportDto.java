package ru.sladkkov.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassportDto {

  @NotEmpty(message = "Серия паспорта не должна быть пустым")
  @Size(min = 4, max = 4)
  @Pattern(regexp = "\\d{4}", message = "Серия паспорта состоит только из цифр")
  private String series;

  @NotEmpty(message = "Номер паспорта не должен быть пустым")
  @Size(min = 6, max = 6)
  @Pattern(regexp = "\\d{6}", message = "Номер паспорта состоит только из цифр")
  private String number;

  @JsonFormat(pattern = "dd-MM-yyyy")
  @NotNull
  private LocalDate issueDate;

  @NotEmpty
  @Size(min = 5, max = 50, message = "Орган выдавший паспорт должен содержать от 5 до 50 символов")
  private String issueBranch;
}
