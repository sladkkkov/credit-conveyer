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
import ru.sladkkov.dto.*;
import ru.sladkkov.service.CalculationCreditService;
import ru.sladkkov.service.ConveyorService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/conveyor")
@Tag(name = "Conveyor Controller", description = "")
// TODO Сделать описание
public class ConveyorController {

  private final ConveyorService conveyorService;
  private final CalculationCreditService calculationCreditService;

  // TODO Сделать описание
  @Operation(summary = "getOffers", description = "")
  @PostMapping("/offers")
  public LoanOfferDtoList getOffers(
      @Valid @RequestBody LoanApplicationRequestDto loanApplicationRequestDto) {

    log.info(
        "LoanApplicationRequestDto поступил в ConveyorController по пути /conveyor/offers. "
            + "loanApplicationRequestDto: {}",
        loanApplicationRequestDto);

    return new LoanOfferDtoList(conveyorService.calculationOffers(loanApplicationRequestDto));
  }

  // TODO Сделать описание
  @Operation(summary = "getSortedOffers", description = "")
  @PostMapping("/offers/sort")
  public LoanOfferDtoList getSortedOffers(
      @Valid @RequestBody LoanApplicationRequestDto loanApplicationRequestDto) {

    log.info(
        "LoanApplicationRequestDto поступил в ConveyorController по пути /conveyor/offers/sort. "
            + "loanApplicationRequestDto: {}",
        loanApplicationRequestDto);

    return new LoanOfferDtoList(
        conveyorService.sortLoanOfferDtoWithGetRate(
            conveyorService.calculationOffers(loanApplicationRequestDto)));
  }

  // TODO Сделать описание
  @Operation(summary = "calculation", description = "")
  @PostMapping("/calculation")
  public CreditDto calculation(@Valid @RequestBody ScoringDataDto scoringDataDto) {

    log.info(
        "ScoringDataDto поступил в ConveyorController по пути /conveyor/calculation. "
            + "scoringDataDto: {}",
        scoringDataDto);

    return calculationCreditService.calculateCreditDto(scoringDataDto);
  }
}
