package ru.sladkkov.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
public class LoanOffer {
  private Long applicationId;
  private BigDecimal requestedAmount;
  private BigDecimal totalAmount;
  private Integer term;
  private BigDecimal monthlyPayment;
  private BigDecimal rate;
  private Boolean isInsuranceEnabled;
  private Boolean isSalaryClient;
}
