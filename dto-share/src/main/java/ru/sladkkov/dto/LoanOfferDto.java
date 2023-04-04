package ru.sladkkov.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanOfferDto {
  private UUID applicationId;
  private BigDecimal requestedAmount;
  private BigDecimal totalAmount;
  private Integer term;
  private BigDecimal monthlyPayment;
  private BigDecimal rate;
  private Boolean isInsuranceEnabled;
  private Boolean isSalaryClient;
}
