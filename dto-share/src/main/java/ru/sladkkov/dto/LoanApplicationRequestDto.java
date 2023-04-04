package ru.sladkkov.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sladkkov.validator.CheckBirthday;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequestDto {

  @NotNull(message = "Сумма кредита не должна быть пустой")
  @DecimalMin(value = "10000.0", message = "Сумма кредита должна быть больше 10000.0")
  private BigDecimal amount;

  @Min(value = 6, message = "Срок кредита должен быть от 6 месяцев")
  @Max(value = 360, message = "Срок кредита должен быть не более 360 месяцев")
  @NotNull(message = "Срок кредита не должен быть пустым")
  private Integer term;

  @Size(min = 2, max = 30, message = "Длина имени должна быть от 2 до 30 символов")
  @NotEmpty(message = "Имя не должен быть пустым")
  @Pattern(regexp = "[A-z]{2,30}", message = "Имя должно состоять из латинский букв")
  private String firstName;

  @Size(min = 2, max = 30, message = "Длина фамилии должна быть от 2 до 30 символов")
  @NotEmpty(message = "Фамилия не должно быть пустым")
  @Pattern(regexp = "[A-z]{2,30}", message = "Фамилия должна состоять из латинский букв")
  private String lastName;

  private String middleName;

  @NotEmpty(message = "Почта не должна быть пустым")
  @Pattern(regexp = "[\\w.]{2,50}@[\\w.]{2,20}", message = "Почта не прошла валидацию")
  private String email;

  @NotNull(message = "Дата рождения не должна быть пустым")
  @Past
  @CheckBirthday
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate birthday;

  private @Valid PassportDto passportDto;
}
