package ru.sladkkov.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ru.sladkkov.enums.CreditStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credit {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private UUID uuid;

  private BigDecimal amount;

  private Integer term;

  private BigDecimal monthlyPayment;

  private BigDecimal rate;

  private BigDecimal psk;

  @OneToMany
  @JoinColumn(name = "payment_schedule_elements_uuid")
  private List<PaymentScheduleElement> paymentScheduleElements;

  private Boolean isInsuranceEnabled;

  private Boolean isSalaryClient;

  private CreditStatus creditStatus;
}
