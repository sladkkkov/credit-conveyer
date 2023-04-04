package ru.sladkkov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sladkkov.dto.CreditDto;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.dto.LoanOfferDtoList;
import ru.sladkkov.dto.ScoringDataDto;
import ru.sladkkov.service.CalculationCreditService;
import ru.sladkkov.service.ConveyorService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/conveyor")
@Tag(name = "Conveyor Controller", description = "Приложение \"Кредитный конвейер\" с применением" +
        "технологий и инструментов:\n" +
        "Java, SpringBoot, PostgreSQL, JPA, Swagger, Kafka, JUnit, Lombok")
public class ConveyorController {

    private final ConveyorService conveyorService;
    private final CalculationCreditService calculationCreditService;

    @Operation(summary = "getOffers", description = "Логика создания 4 кредитных предложений  " +
            "(сущность \"LoanOffer\") по кредиту с разными условиями (без страховки, со" +
            " страховкой, с зарплатным клиентом, со страховкой и зарплатным клиентом) или отказ.\n")
    @PostMapping("/offers")
    public LoanOfferDtoList calculationPossibleLoanOffer(
            @Valid @RequestBody LoanApplicationRequestDto loanApplicationRequestDto) {

        log.info(
                "LoanApplicationRequestDto поступил в ConveyorController по пути /conveyor/offers. "
                        + "loanApplicationRequestDto: {}", loanApplicationRequestDto);

        return new LoanOfferDtoList(conveyorService.calculationOffers(loanApplicationRequestDto));
    }

    @Operation(summary = "getSortedOffers", description = "Возвращает отсортированный список по " +
            "увелечению кредитной ставки.")
    @PostMapping("/offers/sort")
    public LoanOfferDtoList getSortedOffers(
            @Valid @RequestBody LoanApplicationRequestDto loanApplicationRequestDto) {

        log.info("LoanApplicationRequestDto поступил в ConveyorController по пути " +
                        "/conveyor/offers/sort.loanApplicationRequestDto: {}",
                loanApplicationRequestDto);

        return new LoanOfferDtoList(conveyorService.sortLoanOfferDtoWithGetRate(
                conveyorService.calculationOffers(loanApplicationRequestDto)));
    }

    @Operation(summary = "calculation", description = "Происходит скоринг данных в КК, КК " +
            "рассчитывает все данные по кредиту (ПСК, график платежей и тд)")
    @PostMapping("/calculation")
    public CreditDto calculation(@Valid @RequestBody ScoringDataDto scoringDataDto) {

        log.info(
                "ScoringDataDto поступил в ConveyorController по пути /conveyor/calculation. "
                        + "scoringDataDto: {}", scoringDataDto);

        return calculationCreditService.calculateCreditDto(scoringDataDto);
    }
}
