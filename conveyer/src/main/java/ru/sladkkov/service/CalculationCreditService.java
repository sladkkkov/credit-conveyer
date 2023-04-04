package ru.sladkkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.CreditDto;
import ru.sladkkov.dto.PaymentScheduleElementDto;
import ru.sladkkov.dto.ScoringDataDto;
import ru.sladkkov.exception.custom.ScoringDataDtoIsNullException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationCreditService {

  private final ScoringService scoringService;

  public CreditDto calculateCreditDto(ScoringDataDto scoringDataDto) {

    log.info("Начинаю создание CreditDto в методе calculateCreditDto");

    if (scoringDataDto == null) {
      log.error("scoringDataDto is null");

      throw new ScoringDataDtoIsNullException("scoringDataDto is null", new NullPointerException());
    }

    var rate = scoringService.scoringData(scoringDataDto);

    var monthlyPayment =
        calculateMonthlyPayment(
            scoringDataDto.getLoanApplicationRequestDto().getTerm(),
            rate,
            scoringDataDto.getLoanApplicationRequestDto().getAmount());

    var paymentScheduleElements =
        calculatePaymentScheduleList(
            scoringDataDto.getLoanApplicationRequestDto().getTerm(),
            rate,
            scoringDataDto.getLoanApplicationRequestDto().getAmount(),
            monthlyPayment);

    var psk =
        calculatePsk(
            paymentScheduleElements, scoringDataDto.getLoanApplicationRequestDto().getAmount());

    CreditDto creditDto =
        CreditDto.builder()
            .amount(scoringDataDto.getLoanApplicationRequestDto().getAmount())
            .term(scoringDataDto.getLoanApplicationRequestDto().getTerm())
            .rate(rate)
            .isSalaryClient(scoringDataDto.getIsSalaryClient())
            .psk(psk)
            .paymentSchedule(paymentScheduleElements)
            .monthlyPayment(monthlyPayment)
            .build();

    log.info(
        "Заканчиваю создание CreditDto. amount = {}, term = {}, rate = {}, isSalaryClient = {},"
            + " psk = {}, paymentSchedule = {}, monthlyPayment = {}",
        scoringDataDto.getLoanApplicationRequestDto().getAmount(),
        scoringDataDto.getLoanApplicationRequestDto().getTerm(),
        rate,
        scoringDataDto.getIsSalaryClient(),
        psk,
        paymentScheduleElements,
        monthlyPayment);

    return creditDto;
  }

  public BigDecimal calculateMonthlyPayment(Integer term, BigDecimal rate, BigDecimal totalAmount) {

    log.info(
        "Начинаю вычисление месячного платежа. Срок = {} месяцев. Процентная ставка = {}% в год."
            + " Сумма кредита = {} рублей",
        term, rate, totalAmount);

    BigDecimal monthlyRate = calculateMonthlyRate(rate);

    BigDecimal monthlyPayment = getMonthlyPayment(term, totalAmount, monthlyRate);

    log.info("Месячный платеж = {} рублей", monthlyPayment);

    return monthlyPayment;
  }

  private BigDecimal calculateMonthlyRate(BigDecimal rate) {

    log.info("Начинаю вычисление месячной ставки кредита. Базовая ставка = {}% в год", rate);

    BigDecimal monthlyRate =
        rate.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP);

    log.info("Месячная ставка кредита = {}% в год", monthlyRate);

    return monthlyRate;
  }

  private static BigDecimal getMonthlyPayment(
      Integer term, BigDecimal totalAmount, BigDecimal monthlyRate) {

    var a = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE);

    var b = BigDecimal.ONE.add(monthlyRate).pow(term).divide(a, 8, RoundingMode.HALF_UP);

    return totalAmount.multiply(monthlyRate.multiply(b));
  }

  public List<PaymentScheduleElementDto> calculatePaymentScheduleList(
      Integer term, BigDecimal rate, BigDecimal creditAmount, BigDecimal monthlyPayment) {

    log.info("Начинаю создание List<PaymentScheduleElement>");

    BigDecimal remainingDebt = creditAmount;

    List<PaymentScheduleElementDto> paymentScheduleElementDtoList = new ArrayList<>();

    for (int i = 1; i <= term; i++) {
      LocalDate paymentDate = LocalDate.now().plusMonths(i);

      BigDecimal interestPayment =
          remainingDebt
              .multiply(rate)
              .multiply(new BigDecimal(paymentDate.lengthOfMonth()))
              .divide(BigDecimal.valueOf(365), 8, RoundingMode.HALF_UP)
              .divide(new BigDecimal(100), 8, RoundingMode.HALF_UP);

      BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
      remainingDebt = remainingDebt.subtract(debtPayment);

      if (i != term) {
        paymentScheduleElementDtoList.add(
            PaymentScheduleElementDto.builder()
                .number(i)
                .date(paymentDate)
                .totalPayment(monthlyPayment)
                .interestPayment(interestPayment)
                .debtPayment(debtPayment)
                .remainingDebt(remainingDebt)
                .build());
      } else {
        paymentScheduleElementDtoList.add(
            PaymentScheduleElementDto.builder()
                .number(i)
                .date(paymentDate)
                .totalPayment(monthlyPayment.add(remainingDebt))
                .interestPayment(interestPayment)
                .debtPayment(debtPayment.add(remainingDebt))
                .remainingDebt(BigDecimal.ZERO)
                .build());
      }

      log.info(
          "Платеж добавлен в список платежей.number: {}, paymentDate: {}, interestPayment: "
              + "{},debtPayment: {}, remainingDebt: {} ",
          i,
          paymentDate,
          interestPayment,
          debtPayment,
          remainingDebt);
    }

    return paymentScheduleElementDtoList;
  }

  public BigDecimal calculateAllInterestPayment(
      List<PaymentScheduleElementDto> paymentScheduleElementDtoList) {

    List<BigDecimal> interestPayments =
        paymentScheduleElementDtoList.stream()
            .map(PaymentScheduleElementDto::getInterestPayment)
            .toList();

    return interestPayments.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal calculatePsk(
      List<PaymentScheduleElementDto> paymentScheduleElementDtoList, BigDecimal creditAmount) {

    List<BigDecimal> payments =
        paymentScheduleElementDtoList.stream()
            .map(PaymentScheduleElementDto::getTotalPayment)
            .collect(toList());

    List<LocalDate> dates =
        paymentScheduleElementDtoList.stream()
            .map(PaymentScheduleElementDto::getDate)
            .collect(toList());

    dates.add(0, LocalDate.now());
    payments.add(0, creditAmount.negate());

    BigDecimal basePeriod = BigDecimal.valueOf(30);
    BigDecimal countBasePeriods =
        BigDecimal.valueOf(365).divide(basePeriod, 3, RoundingMode.HALF_UP);

    List<BigDecimal> daysSinceDeliveryToEachPayment = new ArrayList<>();

    for (int k = 0; k <= paymentScheduleElementDtoList.size(); k++) {
      int daysToPayment =
          (int) Duration.between(dates.get(0).atStartOfDay(), dates.get(k).atStartOfDay()).toDays();
      daysSinceDeliveryToEachPayment.add(BigDecimal.valueOf(daysToPayment));
    }

    List<BigDecimal> eList = new ArrayList<>();
    List<BigDecimal> qList = new ArrayList<>();

    for (int k = 0; k <= paymentScheduleElementDtoList.size(); k++) {
      eList.add(
          daysSinceDeliveryToEachPayment
              .get(k)
              .remainder(basePeriod)
              .divide(basePeriod, 3, RoundingMode.HALF_UP));

      qList.add(daysSinceDeliveryToEachPayment.get(k).divide(basePeriod, 3, RoundingMode.FLOOR));
    }

    BigDecimal accuracy = BigDecimal.valueOf(0.00001);

    BigDecimal sum = BigDecimal.ONE;

    BigDecimal i = BigDecimal.ZERO;

    while (sum.doubleValue() > 0) {

      sum = BigDecimal.ZERO;
      for (int k = 0; k <= paymentScheduleElementDtoList.size(); k++) {

        BigDecimal firstPartDivider = eList.get(k).multiply(i).add(BigDecimal.ONE);
        BigDecimal secondPartDivider = (new BigDecimal(1).add(i)).pow(qList.get(k).intValue());

        sum =
            payments
                .get(k)
                .divide(
                    (firstPartDivider.multiply(secondPartDivider, MathContext.DECIMAL64)),
                    7,
                    RoundingMode.HALF_EVEN)
                .add(sum);
      }

      i = i.add(accuracy);
    }

    return i.multiply(countBasePeriods).multiply(BigDecimal.valueOf(100));
  }
}
