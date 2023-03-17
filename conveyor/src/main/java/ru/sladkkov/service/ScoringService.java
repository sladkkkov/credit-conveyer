package ru.sladkkov.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.ScoringData;
import ru.sladkkov.enums.EmploymentStatus;
import ru.sladkkov.enums.Gender;
import ru.sladkkov.enums.MartialStatus;
import ru.sladkkov.enums.PositionAtWork;
import ru.sladkkov.exception.*;

@Service
@Slf4j
public class ScoringService {

  @Value("${baseRate}")
  private BigDecimal baseRate;

  public BigDecimal scoringData(ScoringData scoringData) {

    log.info("Скоринг занятости, rate: {}", baseRate);

    BigDecimal rate = baseRate;

    rate = rate.add(checkEmploymentStatus(scoringData));

    rate = rate.add(checkMartialStatus(scoringData));

    rate = rate.add(checkWorkPosition(scoringData));

    rate = rate.add(checkDependentAmount(scoringData));

    rate = rate.add(checkAgeWithGender(scoringData));

    rate = rate.add(checkEmployeeSalary(scoringData));

    rate = rate.add(calculateRate(scoringData));

    checkWorkExperience(scoringData);

    log.info("Скоринг занятости, rate: {}", rate);

    return rate;
  }

  public BigDecimal checkEmploymentStatus(final ScoringData scoringData) {
    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг занятости начался. Добавочный additionalRate: {}", additionalRate);

    var employmentStatus = scoringData.getEmployment().getEmploymentStatus();

    if (employmentStatus.equals(EmploymentStatus.UNEMPLOYED)) {
      log.error("Рабочий статус клиента - безработный. Отказ.");

      throw new UnemployedException("Отказ по причине безработный", new IllegalAccessException());
    }

    if (employmentStatus.equals(EmploymentStatus.EMPLOYED)) {
      additionalRate = additionalRate.add(BigDecimal.valueOf(0.1));
    }

    if (employmentStatus.equals(EmploymentStatus.BUSINESS_OWNER)) {
      additionalRate = additionalRate.add(BigDecimal.valueOf(0.3));
    }

    log.info("Скоринг занятости закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal checkMartialStatus(final ScoringData scoringData) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг семейного положения начался. Добавочный additionalRate: {}", additionalRate);

    var maritalStatus = scoringData.getMaritalStatus();

    if (maritalStatus.equals(MartialStatus.MARRIED)) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    if (maritalStatus.equals(MartialStatus.WIDOWED)) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.5));
    }

    if (maritalStatus.equals(MartialStatus.DIVORCED)) {
      additionalRate = additionalRate.add(BigDecimal.valueOf(0.3));
    }

    log.info(
        "Скоринг семейного положения закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal checkWorkPosition(final ScoringData scoringData) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг рабочей позиции начался. Добавочный additionalRate: {}", additionalRate);

    var position = scoringData.getEmployment().getPosition();

    if (position.equals(PositionAtWork.MIDDLE_MANAGER)) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.2));
    }

    if (position.equals(PositionAtWork.TOP_MANAGER)) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.4));
    }

    log.info("Скоринг рабочей позиции закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal checkAgeWithGender(final ScoringData scoringData) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг возраста и пола начался. Добавочный additionalRate: {}", additionalRate);

    Period period =
        Period.between( scoringData.getLoanApplicationRequest().getBirthday(), LocalDate.now());

    var years = period.getYears();

    if (years < 20) {
      log.error("Возраст сотрудника не подходит под условия кредита. Отказ.");

      throw new AgeIsLessException(
          "Возраст не подходит под условия кредита. Отказ.", new IllegalAccessException());
    }

    if (years > 60) {
      log.error("Возраст сотрудника не подходит под условия кредита. Отказ.");

      throw new AgeIsMoreException(
          "Возраст не подходит под условия кредита {}. Отказ.", new IllegalAccessException());
    }

    if (!scoringData.getGender().equals(Gender.MALE) && years > 30 || years < 55) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    if (scoringData.getGender().equals(Gender.FEMALE) && years > 35 || years < 60) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    log.info("Скоринг возраста и пола закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public void checkWorkExperience(final ScoringData scoringData) {

    log.info("Скоринг опыта работы начался");

    var workExperienceTotal = scoringData.getEmployment().getWorkExperienceTotal();

    if (workExperienceTotal < 12) {
      log.error(
          "Недостаточно общего опыта работы, заявка отклонена workExperienceTotal {} < 12",
          workExperienceTotal);

      throw new WorkExperienceNotEnoughException(
          "Недостаточно общего опыта работы для одобрения кредита.", new IllegalAccessException());
    }

    if (scoringData.getEmployment().getWorkExperienceCurrent() < 3) {
      log.error(
          "Недостаточно текущего опыта работы, заявка отклонена workExperienceTotal {} < 3",
          workExperienceTotal);

      throw new WorkExperienceNotEnoughException(
          "Недостаточно текущего опыта работы для одобрения кредита.",
          new IllegalAccessException());
    }

    log.info("Скоринг опыта работы закончился");
  }

  public BigDecimal checkDependentAmount(final ScoringData scoringData) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info(
        "Скоринг количества иждивенцов начался. Добавочный additionalRate: {}", additionalRate);

    if (scoringData.getDependentAmount() > 1) {
      additionalRate = additionalRate.add(BigDecimal.valueOf(0.1));
    }

    log.info(
        "Скоринг количества иждивенцов закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal checkEmployeeSalary(final ScoringData scoringData) {
    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг зарплаты начинается. Добавочный additionalRate: {}", additionalRate);

    if (scoringData
            .getLoanApplicationRequest()
            .getAmount()
            .compareTo(scoringData.getEmployment().getSalary().multiply(BigDecimal.valueOf(20)))
        >= 0) {

      log.error("Размер кредита превышает 20 зарплат. Отказ");

      throw new CreditIsVeryBigException(
          "Размер кредита превышает 20 зарплат", new IllegalAccessException());
    }

    log.info("Скоринг зарплаты закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal calculateRate(final ScoringData scoringData) {
    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг начинается. Добавочный additionalRate: {}", additionalRate);

    if (Boolean.TRUE.equals(scoringData.getIsInsuranceEnabled())) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    if (Boolean.TRUE.equals(scoringData.getIsSalaryClient())) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.1));
    }

    log.info("Скоринг закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }
}
