package ru.sladkkov.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sladkkov.dto.FinishRegistrationRequestDto;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.dto.LoanOfferDto;
import ru.sladkkov.dto.LoanOfferDtoList;
import ru.sladkkov.service.ApplicationFacade;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
    value = "/deal",
    produces = {"application/json"})
public class DealController {

  private final ApplicationFacade applicationFacade;

  @PostMapping("/application")
  public ResponseEntity<LoanOfferDtoList> getAllCondition(
      @Valid @RequestBody LoanApplicationRequestDto loanApplicationRequestDto) {

    return new ResponseEntity<>(
        applicationFacade.createApplicationAndSaveEntity(loanApplicationRequestDto), HttpStatus.OK);
  }

  @PutMapping("/offer")
  public void chooseOffer(@Valid @RequestBody LoanOfferDto loanOfferDto) {

    applicationFacade.configureApplicationId(loanOfferDto);
  }

  @PutMapping("/calculate/{applicationId}")
  public void calculateOffer(
      @Valid @RequestBody FinishRegistrationRequestDto finishRegistrationRequest,
      @PathVariable UUID applicationId) {

    applicationFacade.configuringScoringDataDto(finishRegistrationRequest, applicationId);
  }
}
