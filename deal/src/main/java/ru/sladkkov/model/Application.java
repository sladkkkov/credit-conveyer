package ru.sladkkov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ru.sladkkov.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private UUID uuid;

  @OneToOne
  @JoinColumn(name = "client_uuid")
  private Client client;

  @OneToOne
  @JoinColumn(name = "credit_uuid")
  private Credit credit;

  @Enumerated(EnumType.STRING)
  private Status status;

  private LocalDateTime creationDate;

  @OneToOne
  @JoinColumn(name = "loan_offer_uuid")
  private LoanOffer appliedOffer;

  private LocalDate signDate;

  private Integer sesCode;

  private LocalDate statusHistory;
}
