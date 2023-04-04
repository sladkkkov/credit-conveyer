package ru.sladkkov.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CreditDto {
    private BigDecimal amount;

    private Integer term;

    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private BigDecimal psk;

    private Boolean isSalaryClient;

  private List<PaymentScheduleElementDto> paymentSchedule;
}
