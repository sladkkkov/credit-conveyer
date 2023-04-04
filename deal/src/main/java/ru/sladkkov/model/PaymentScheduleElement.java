package ru.sladkkov.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentScheduleElement {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private UUID uuid;

  private Integer number;
  private LocalDate date;
  private BigDecimal totalPayment;
  private BigDecimal interestPayment;
  private BigDecimal debtPayment;
  private BigDecimal remainingDebt;

  @ManyToOne
  @JoinColumn(name = "credit_uuid")
  private Credit credit;
}
