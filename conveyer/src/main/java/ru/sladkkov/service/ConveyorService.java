package ru.sladkkov.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.dto.LoanOfferDto;
import ru.sladkkov.exception.custom.LoanApplicationRequestDtoIsNullException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class ConveyorService {

    @Value("${baseRate}")
    private BigDecimal baseRate;

    private static BigDecimal getMonthlyPayment(
            Integer term, BigDecimal totalAmount, BigDecimal monthlyRate) {

        var a = BigDecimal.ONE.add(monthlyRate).pow(term).subtract(BigDecimal.ONE);
        var b = BigDecimal.ONE.add(monthlyRate).pow(term).divide(a, 8, RoundingMode.HALF_UP);

        return totalAmount.multiply(monthlyRate.multiply(b));
    }

    public List<LoanOfferDto> calculationOffers(
            final LoanApplicationRequestDto loanApplicationRequestDto) {

        log.info("Начинается создание кредитных предложений");

        if (loanApplicationRequestDto == null) {

            log.error("LoanApplicationRequestDTO is null. LoanApplicationRequestDtoIsNullException exception");
            throw new LoanApplicationRequestDtoIsNullException("LoanApplicationRequestDTO is null",
                    new NullPointerException());
        }

        return getCombinationLoanOfferDto(loanApplicationRequestDto);
    }

    public List<LoanOfferDto> sortLoanOfferDtoWithGetRate(List<LoanOfferDto> loanOfferDtoList) {

        loanOfferDtoList.sort(Comparator.comparing(LoanOfferDto::getRate));

        return loanOfferDtoList;
    }

    private List<LoanOfferDto> getCombinationLoanOfferDto(
            LoanApplicationRequestDto loanApplicationRequestDto) {

        List<LoanOfferDto> loanOfferDtoList = new ArrayList<>();

        loanOfferDtoList.add(createLoanOfferDto(loanApplicationRequestDto, true, true));
        loanOfferDtoList.add(createLoanOfferDto(loanApplicationRequestDto, true, false));
        loanOfferDtoList.add(createLoanOfferDto(loanApplicationRequestDto, false, true));
        loanOfferDtoList.add(createLoanOfferDto(loanApplicationRequestDto, false, false));

        return loanOfferDtoList;
    }

    public LoanOfferDto createLoanOfferDto(
            LoanApplicationRequestDto loanApplicationRequestDto,
            boolean isInsuranceEnabled,
            boolean isSalaryClient) {

        log.info(
                "Создание предложения. isInsuranceEnabled : {},  isSalaryClient: {}",
                isInsuranceEnabled,
                isSalaryClient);

        if (loanApplicationRequestDto == null) {

            log.error(
                    "LoanApplicationRequestDTO is null. LoanApplicationRequestDtoIsNullException exception");
            throw new LoanApplicationRequestDtoIsNullException(
                    "LoanApplicationRequestDTO is null", new NullPointerException());
        }

        BigDecimal currentRate = calculateRate(isInsuranceEnabled, isSalaryClient);

        BigDecimal monthlyPayment =
                calculateMonthlyPayment(
                        loanApplicationRequestDto.getTerm(),
                        currentRate,
                        loanApplicationRequestDto.getAmount());

        return LoanOfferDto.builder()
                .requestedAmount(loanApplicationRequestDto.getAmount())
                .totalAmount(loanApplicationRequestDto.getAmount())
                .term(loanApplicationRequestDto.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(currentRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    private BigDecimal calculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {

        BigDecimal rate = baseRate;

        log.info("Начинаю вычисления ставки кредита, базовая ставка = {}% в год", baseRate);

        if (Boolean.TRUE.equals(isInsuranceEnabled)) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }

        if (Boolean.TRUE.equals(isSalaryClient)) {
            rate = rate.subtract(BigDecimal.valueOf(2));
        }

        log.info("Вычисленная процентная ставка кредита = {}% в год", rate);

        return rate;
    }

    private BigDecimal calculateMonthlyRate(BigDecimal rate) {

        log.info("Начинаю вычисление месячной ставки кредита. Базовая ставка = {}% в год", rate);

        BigDecimal monthlyRate =
                rate.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP);

        log.info("Месячная ставка кредита = {}% в год", monthlyRate);

        return monthlyRate;
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
}

