package ru.sladkkov.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.sladkkov.enums.EmploymentStatus;
import ru.sladkkov.enums.PositionAtWork;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employment {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private UUID uuid;

  private EmploymentStatus employmentStatus;

  @NotEmpty
  @Pattern(regexp = "\\d+", message = "ИНН состоит только из цифр")
  @Size(max = 12, message = "Количество цифр ИНН не может быть больше 12")
  private String employerINN;

  @Enumerated(EnumType.STRING)
  private PositionAtWork position;

  @NotNull private BigDecimal salary;

  @NotNull
  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer workExperienceTotal;

  @NotNull
  @Min(value = 0, message = "Опыт работы не может быть отрицательным")
  private Integer workExperienceCurrent;

  @ManyToMany
  @JoinColumn(name = "client_uuid")
  private List<Client> clients;
}
