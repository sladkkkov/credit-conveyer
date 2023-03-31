package ru.sladkkov.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ru.sladkkov.enums.Gender;
import ru.sladkkov.enums.MartialStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private UUID uuid;

  private String lastName;

  private String firstName;

  private String middleName;

  private LocalDate birthdate;

  @Column(unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private MartialStatus martialStatus;

  private Integer dependentAmount;

  @OneToOne
  @JoinColumn(name = "passport_uuid", referencedColumnName = "uuid", unique = true)
  private Passport passport;

  @ManyToMany
  @JoinColumn(name = "uuid1", referencedColumnName = "uuid")
  private List<Employment> employment;

  private Integer account;
}
