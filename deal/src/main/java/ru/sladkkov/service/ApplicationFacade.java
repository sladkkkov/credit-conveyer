package ru.sladkkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sladkkov.dto.*;
import ru.sladkkov.enums.Status;
import ru.sladkkov.mapper.ClientMapper;
import ru.sladkkov.mapper.CreditMapper;
import ru.sladkkov.mapper.LoanOfferDtoMapper;
import ru.sladkkov.mapper.PassportMapper;
import ru.sladkkov.model.Application;
import ru.sladkkov.model.Client;
import ru.sladkkov.model.Passport;
import ru.sladkkov.repository.CreditRepository;
import ru.sladkkov.repository.LoanOfferRepository;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationFacade {

    private final ClientService clientService;
    private final ApplicationService applicationService;
    private final CreditRepository creditRepository;
    private final RestTemplate restTemplate;
    private final LoanOfferDtoMapper loanOfferDtoMapper;
    private final CreditMapper creditMapper;
    private final PassportMapper passportMapper;
    private final ClientMapper clientMapper;
    // TODO сделать сеовисный слой
    private final LoanOfferRepository loanOfferRepository;
    private static final String CONVEYOR_OFFERS_SORTED = "http://localhost:7652/conveyor/offers/sort";
    private static final String CONVEYOR_CALCULATION = "http://localhost:7652/conveyor/calculation";

    public LoanOfferDtoList createApplicationAndSaveEntity(
            LoanApplicationRequestDto loanApplicationRequestDto) {
        var client = clientService.saveClient(loanApplicationRequestDto);

        var application = applicationService.configureApplication(client);
        applicationService.saveApplication(application);

        HttpEntity<LoanApplicationRequestDto> request = new HttpEntity<>(loanApplicationRequestDto);
        var loanOfferDtoList =
                restTemplate.postForEntity(CONVEYOR_OFFERS_SORTED, request, LoanOfferDtoList.class);

        Objects.requireNonNull(loanOfferDtoList.getBody())
                .getLoanOfferDtoListWrapper()
                .forEach(t -> t.setApplicationId(application.getUuid()));

        return loanOfferDtoList.getBody();
    }

    public Application configureApplicationId(LoanOfferDto loanOfferDto) {
        var application = applicationService.findById(loanOfferDto.getApplicationId());
        // TODO: 27.03.2023 Сделать обновление статуса заявки
        // TODO: 27.03.2023 Сделать историю статусов( List<ApplicationStatusHistoryDTO> )

        application.setStatus(Status.CC_APPROVED);

        var loanOffer = loanOfferDtoMapper.toModel(loanOfferDto);

        loanOfferRepository.save(loanOffer);
        application.setAppliedOffer(loanOffer);

        return applicationService.saveApplication(application);
    }

    public void configuringScoringDataDto(
            FinishRegistrationRequestDto finishRegistrationRequestDto, UUID applicationUUID) {
        var application = applicationService.findById(applicationUUID);

        Passport passport = application.getClient().getPassport();

        Client client = application.getClient();

        passport.setIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch());
        passport.setIssueDate(finishRegistrationRequestDto.getPassportIssueDate());

        var loanApplicationRequestDto = clientMapper.clientToLoanApplicationRequestDto(client);

        var scoringDataDto =
                ScoringDataDto.builder()
                        .gender(finishRegistrationRequestDto.getGender())
                        .maritalStatus(finishRegistrationRequestDto.getMartialStatus())
                        .dependentAmount(finishRegistrationRequestDto.getDependentAmount())
                        .passportDto(passportMapper.toDto(passport))
                        .employmentDto(finishRegistrationRequestDto.getEmployment())
                        .account(finishRegistrationRequestDto.getAccount())
                        .loanApplicationRequestDto(loanApplicationRequestDto)
                        .build();

        HttpEntity<ScoringDataDto> request = new HttpEntity<>(scoringDataDto);

        var creditDtoResponseEntity =
                restTemplate.postForEntity(CONVEYOR_CALCULATION, request, CreditDto.class);

        var credit = creditMapper.toModel(Objects.requireNonNull(creditDtoResponseEntity.getBody()));

        creditRepository.save(credit);
        application.setCredit(credit);

        applicationService.saveApplication(application);
    }
}
