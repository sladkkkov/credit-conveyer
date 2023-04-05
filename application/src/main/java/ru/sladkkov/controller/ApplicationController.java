package ru.sladkkov.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.dto.LoanOfferDto;
import ru.sladkkov.dto.LoanOfferDtoList;

@RestController("/application")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    @Value("${server.deal.port}")
    private static String dealPort;
    private static final String DEAL_APPLICATION_OFFER = "http://localhost:" + dealPort + "/deal/offer";
    private static final String DEAL_APPLICATION = "http://localhost:" + dealPort + "/deal/application";
    private final RestTemplate restTemplate;

    @Operation(summary = "prescoring", description = "МС Заявка осуществляет прескоринг заявки и если прескоринг проходит, то заявка сохраняется в МС" +
            "Сделка и отправляется в КК.\n")
    @PostMapping
    public LoanOfferDtoList prescoringLoanApplicationRequestDto(
            @RequestBody @Valid LoanApplicationRequestDto loanApplicationRequestDto) {

        log.info(
                "LoanApplicationRequestDto поступил в ConveyorController по пути /conveyor/offers. "
                        + "loanApplicationRequestDto: {}",
                loanApplicationRequestDto);

        HttpEntity<LoanApplicationRequestDto> request = new HttpEntity<>(loanApplicationRequestDto);

        var loanOfferDtoResponseEntity = restTemplate.postForEntity(DEAL_APPLICATION, request, LoanOfferDtoList.class);

        return loanOfferDtoResponseEntity.getBody();
    }

    @Operation(summary = "prescoring", description = "Пользователь выбирает одно из предложений," +
            " отправляется запрос в МС Заявка, а оттуда в МС Сделка, где заявка на кредит и сам " +
            "кредит сохраняются в базу. Сделка и отправляется в КК.\n")
    @PostMapping("offer")
    public void getApplication(LoanOfferDto loanOfferDto) {

        HttpEntity<LoanOfferDto> request = new HttpEntity<>(loanOfferDto);

        restTemplate.postForLocation(DEAL_APPLICATION_OFFER, request);
    }
}
