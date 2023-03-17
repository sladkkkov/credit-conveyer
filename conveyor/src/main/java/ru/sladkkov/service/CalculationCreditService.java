package ru.sladkkov.service;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.Credit;
import ru.sladkkov.dto.PaymentScheduleElement;
import ru.sladkkov.dto.ScoringData;
import ru.sladkkov.exception.ScoringDataDtoIsNullException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculationCreditService {

  private final ConveyorService conveyorService;
  private final ScoringService scoringService;

  public Credit calculateCreditDto(ScoringData scoringData) {

    log.info("Начинаю создание CreditDto в методе calculateCreditDto");

    if (scoringData == null) {
      log.error("scoringDataDto is null");

      throw new ScoringDataDtoIsNullException("scoringDataDto is null", new NullPointerException());
    }

    var rate = scoringService.scoringData(scoringData);

    var monthlyPayment =
        conveyorService.calculateMonthlyPayment(
            scoringData.getLoanApplicationRequest().getTerm(),
            rate,
            scoringData.getLoanApplicationRequest().getAmount());

    var paymentScheduleElements =
        calculatePaymentScheduleList(
            scoringData.getLoanApplicationRequest().getTerm(),
            rate,
            scoringData.getLoanApplicationRequest().getAmount(),
            monthlyPayment);

    var psk =
        calculatePsk(
            paymentScheduleElements, scoringData.getLoanApplicationRequest().getAmount());

    Credit credit =
        Credit.builder()
            .amount(scoringData.getLoanApplicationRequest().getAmount())
            .term(scoringData.getLoanApplicationRequest().getTerm())
            .rate(rate)
            .isSalaryClient(scoringData.getIsSalaryClient())
            .psk(psk)
            .paymentSchedule(paymentScheduleElements)
            .monthlyPayment(monthlyPayment)
            .build();

    log.info(
        "Заканчиваю создание CreditDto. amount = {}, term = {}, rate = {}, isSalaryClient = {},"
            + " psk = {}, paymentSchedule = {}, monthlyPayment = {}",
        scoringData.getLoanApplicationRequest().getAmount(),
        scoringData.getLoanApplicationRequest().getTerm(),
        rate,
        scoringData.getIsSalaryClient(),
        psk,
        paymentScheduleElements,
        monthlyPayment);

    return credit;
  }

  public List<PaymentScheduleElement> calculatePaymentScheduleList(
      Integer term, BigDecimal rate, BigDecimal creditAmount, BigDecimal monthlyPayment) {

    log.info("Начинаю создание List<PaymentScheduleElement>");

    BigDecimal remainingDebt = creditAmount;

    List<PaymentScheduleElement> paymentScheduleElementList = new ArrayList<>();

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
        paymentScheduleElementList.add(
            PaymentScheduleElement.builder()
                .number(i)
                .date(paymentDate)
                .totalPayment(monthlyPayment)
                .interestPayment(interestPayment)
                .debtPayment(debtPayment)
                .remainingDebt(remainingDebt)
                .build());
      } else {
        paymentScheduleElementList.add(
            PaymentScheduleElement.builder()
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

    return paymentScheduleElementList;
  }

  public BigDecimal calculateAllInterestPayment(
      List<PaymentScheduleElement> paymentScheduleElementList) {

    List<BigDecimal> interestPayments =
        paymentScheduleElementList.stream()
            .map(PaymentScheduleElement::getInterestPayment)
            .toList();

    return interestPayments.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal calculatePsk(
      List<PaymentScheduleElement> paymentScheduleElementList, BigDecimal creditAmount) {

    List<BigDecimal> payments =
        paymentScheduleElementList.stream()
            .map(PaymentScheduleElement::getTotalPayment)
            .collect(toList());

    List<LocalDate> dates =
        paymentScheduleElementList.stream().map(PaymentScheduleElement::getDate).collect(toList());

    dates.add(0, LocalDate.now());
    payments.add(0, creditAmount.negate());

    BigDecimal basePeriod = BigDecimal.valueOf(30);
    BigDecimal countBasePeriods =
        BigDecimal.valueOf(365).divide(basePeriod, 3, RoundingMode.HALF_UP);

    List<BigDecimal> daysSinceDeliveryToEachPayment = new ArrayList<>();

    for (int k = 0; k <= paymentScheduleElementList.size(); k++) {
      int daysToPayment =
          (int) Duration.between(dates.get(0).atStartOfDay(), dates.get(k).atStartOfDay()).toDays();
      daysSinceDeliveryToEachPayment.add(BigDecimal.valueOf(daysToPayment));
    }

    List<BigDecimal> eList = new ArrayList<>();
    List<BigDecimal> qList = new ArrayList<>();

    for (int k = 0; k <= paymentScheduleElementList.size(); k++) {
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
      for (int k = 0; k <= paymentScheduleElementList.size(); k++) {

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
