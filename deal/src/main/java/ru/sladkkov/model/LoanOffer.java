package ru.sladkkov.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoanOffer {

  @Id
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @GeneratedValue(generator = "uuid")
  private UUID uuid;

  private UUID applicationId;

  private BigDecimal requestedAmount;

  private BigDecimal totalAmount;

  private Integer term;

  private BigDecimal monthlyPayment;

  private BigDecimal rate;

  private Boolean isInsuranceEnabled;

  private Boolean isSalaryClient;
}
