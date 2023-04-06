package ru.sladkkov.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sladkkov.dto.FinishRegistrationRequestDto;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.dto.LoanOfferDto;
import ru.sladkkov.dto.LoanOfferDtoList;
import ru.sladkkov.model.Application;
import ru.sladkkov.service.ApplicationFacade;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(
        value = "/deal")
public class DealController {

    private final ApplicationFacade applicationFacade;

    @PostMapping("/application")
    public ResponseEntity<LoanOfferDtoList> getAllCondition(
            @RequestBody LoanApplicationRequestDto loanApplicationRequestDto) {

        return new ResponseEntity<>(
                applicationFacade.createApplicationAndSaveEntity(loanApplicationRequestDto), HttpStatus.OK);
    }

    @PutMapping("/offer")
    public void chooseOffer(@RequestBody LoanOfferDto loanOfferDto) {

        applicationFacade.configureApplicationId(loanOfferDto);
    }

    @PutMapping("/calculate/{applicationId}")
    public void calculateOffer(
            @Valid @RequestBody FinishRegistrationRequestDto finishRegistrationRequest,
            @PathVariable UUID applicationId) {

        applicationFacade.configuringScoringDataDto(finishRegistrationRequest, applicationId);
    }

    //TODO получить все заявки по id
    @GetMapping("admin/application/{applicationId}")
    public Application getApplicationById(@PathVariable UUID applicationId) {

        return new Application();
    }

    //TODO получить все заявки
    @GetMapping("admin/application")
    public List<Application> getAllApplication() {

        return List.of();
    }
}
