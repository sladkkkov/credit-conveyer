package ru.sladkkov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sladkkov.dto.Credit;
import ru.sladkkov.dto.LoanApplicationRequest;
import ru.sladkkov.dto.LoanOffer;
import ru.sladkkov.dto.ScoringData;
import ru.sladkkov.service.CalculationCreditService;
import ru.sladkkov.service.ConveyorService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/conveyor")
@Tag(name = "Conveyor Controller", description = "")
// TODO Сделать описание
public class ConveyorController {

  public static final String GET_OFFERS = "/offers";
  public static final String GET_OFFERS_WITH_SORT = "/offers/sort";
  private static final String CALCULATION_OFFER = "/calculation";
  private final ConveyorService conveyorService;

  private final CalculationCreditService calculationCreditService;

  // TODO Сделать описание
  @Operation(summary = "getOffers", description = "")
  @PostMapping(GET_OFFERS)
  public List<LoanOffer> getOffers(
      @Valid @RequestBody LoanApplicationRequest loanApplicationRequest) {

    log.info(
        "LoanApplicationRequestDto поступил в ConveyorController по пути /conveyor/offers. "
            + "loanApplicationRequestDto: {}",
        loanApplicationRequest);

    return conveyorService.calculationOffers(loanApplicationRequest);
  }

  // TODO Сделать описание
  @Operation(summary = "getSortedOffers", description = "")
  @PostMapping(GET_OFFERS_WITH_SORT)
  public List<LoanOffer> getSortedOffers(
      @Valid @RequestBody LoanApplicationRequest loanApplicationRequest) {

    log.info(
        "LoanApplicationRequestDto поступил в ConveyorController по пути /conveyor/offers/sort. "
            + "loanApplicationRequestDto: {}",
        loanApplicationRequest);

    return conveyorService.sortLoanOfferDtoWithGetRate(
        conveyorService.calculationOffers(loanApplicationRequest));
  }

  // TODO Сделать описание
  @Operation(summary = "calculation", description = "")
  @PostMapping(CALCULATION_OFFER)
  public Credit calculation(@Valid @RequestBody ScoringData scoringData) {

    return calculationCreditService.calculateCreditDto(scoringData);
  }
}
