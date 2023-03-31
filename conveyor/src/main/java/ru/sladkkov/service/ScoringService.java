package ru.sladkkov.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.ScoringDataDto;
import ru.sladkkov.enums.EmploymentStatus;
import ru.sladkkov.enums.Gender;
import ru.sladkkov.enums.MartialStatus;
import ru.sladkkov.enums.PositionAtWork;
import ru.sladkkov.exception.custom.*;

@Service
@Slf4j
public class ScoringService {

  @Value("${baseRate}")
  private BigDecimal baseRate;

  public BigDecimal scoringData(ScoringDataDto scoringDataDto) {

    log.info("Скоринг занятости, rate: {}", baseRate);

    BigDecimal rate = baseRate;

    rate = rate.add(checkEmploymentStatus(scoringDataDto));

    rate = rate.add(checkMartialStatus(scoringDataDto));

    rate = rate.add(checkWorkPosition(scoringDataDto));

    rate = rate.add(checkDependentAmount(scoringDataDto));

    rate = rate.add(checkAgeWithGender(scoringDataDto));

    rate = rate.add(checkEmployeeSalary(scoringDataDto));

    rate = rate.add(calculateRate(scoringDataDto));

    checkWorkExperience(scoringDataDto);

    log.info("Скоринг занятости, rate: {}", rate);

    return rate;
  }

  public BigDecimal checkEmploymentStatus(final ScoringDataDto scoringDataDto) {
    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг занятости начался. Добавочный additionalRate: {}", additionalRate);

    var employmentStatus = scoringDataDto.getEmploymentDto().getEmploymentStatus();

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

  public BigDecimal checkMartialStatus(final ScoringDataDto scoringDataDto) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг семейного положения начался. Добавочный additionalRate: {}", additionalRate);

    var maritalStatus = scoringDataDto.getMaritalStatus();

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

  public BigDecimal checkWorkPosition(final ScoringDataDto scoringDataDto) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг рабочей позиции начался. Добавочный additionalRate: {}", additionalRate);

    var position = scoringDataDto.getEmploymentDto().getPosition();

    if (position.equals(PositionAtWork.MIDDLE_MANAGER)) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.2));
    }

    if (position.equals(PositionAtWork.TOP_MANAGER)) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.4));
    }

    log.info("Скоринг рабочей позиции закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal checkAgeWithGender(final ScoringDataDto scoringDataDto) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг возраста и пола начался. Добавочный additionalRate: {}", additionalRate);

    Period period =
        Period.between(scoringDataDto.getLoanApplicationRequestDto().getBirthday(), LocalDate.now());

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

    if (!scoringDataDto.getGender().equals(Gender.MALE) && years > 30 || years < 55) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    if (scoringDataDto.getGender().equals(Gender.FEMALE) && years > 35 || years < 60) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    log.info("Скоринг возраста и пола закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public void checkWorkExperience(final ScoringDataDto scoringDataDto) {

    log.info("Скоринг опыта работы начался");

    var workExperienceTotal = scoringDataDto.getEmploymentDto().getWorkExperienceTotal();

    if (workExperienceTotal < 12) {
      log.error(
          "Недостаточно общего опыта работы, заявка отклонена workExperienceTotal {} < 12",
          workExperienceTotal);

      throw new WorkExperienceNotEnoughException(
          "Недостаточно общего опыта работы для одобрения кредита.", new IllegalAccessException());
    }

    if (scoringDataDto.getEmploymentDto().getWorkExperienceCurrent() < 3) {
      log.error(
          "Недостаточно текущего опыта работы, заявка отклонена workExperienceTotal {} < 3",
          workExperienceTotal);

      throw new WorkExperienceNotEnoughException(
          "Недостаточно текущего опыта работы для одобрения кредита.",
          new IllegalAccessException());
    }

    log.info("Скоринг опыта работы закончился");
  }

  public BigDecimal checkDependentAmount(final ScoringDataDto scoringDataDto) {

    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info(
        "Скоринг количества иждивенцов начался. Добавочный additionalRate: {}", additionalRate);

    if (scoringDataDto.getDependentAmount() > 1) {
      additionalRate = additionalRate.add(BigDecimal.valueOf(0.1));
    }

    log.info(
        "Скоринг количества иждивенцов закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal checkEmployeeSalary(final ScoringDataDto scoringDataDto) {
    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг зарплаты начинается. Добавочный additionalRate: {}", additionalRate);

    if (scoringDataDto
            .getLoanApplicationRequestDto()
            .getAmount()
            .compareTo(scoringDataDto.getEmploymentDto().getSalary().multiply(BigDecimal.valueOf(20)))
        >= 0) {

      log.error("Размер кредита превышает 20 зарплат. Отказ");

      throw new CreditIsVeryBigException(
          "Размер кредита превышает 20 зарплат", new IllegalAccessException());
    }

    log.info("Скоринг зарплаты закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }

  public BigDecimal calculateRate(final ScoringDataDto scoringDataDto) {
    BigDecimal additionalRate = BigDecimal.ZERO;

    log.info("Скоринг начинается. Добавочный additionalRate: {}", additionalRate);

    if (Boolean.TRUE.equals(scoringDataDto.getIsInsuranceEnabled())) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.3));
    }

    if (Boolean.TRUE.equals(scoringDataDto.getIsSalaryClient())) {
      additionalRate = additionalRate.subtract(BigDecimal.valueOf(0.1));
    }

    log.info("Скоринг закончился. Добавочный additionalRate: {}", additionalRate);

    return additionalRate;
  }
}
