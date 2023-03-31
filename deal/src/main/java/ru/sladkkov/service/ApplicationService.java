package ru.sladkkov.service;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sladkkov.model.Application;
import ru.sladkkov.model.Client;
import ru.sladkkov.repository.ApplicationRepository;
import ru.sladkkov.repository.LoanOfferRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final LoanOfferRepository loanOfferRepository;

  public Application saveApplication(Application application) {
    return applicationRepository.save(application);
  }

  public Application configureApplication(Client client) {
    Application application = new Application();
    application.setClient(client);
    application.setCreationDate(LocalDateTime.now());

    return application;
  }

  public Application findById(UUID uuid) {

    return applicationRepository.findById(uuid).orElseThrow(RuntimeException::new);
  }
}
