package ru.sladkkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.exception.UserAlreadyExistException;
import ru.sladkkov.mapper.ClientMapper;
import ru.sladkkov.model.Client;
import ru.sladkkov.repository.ClientRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final PassportService passportService;
    private final ClientMapper clientMapper;

    public Client saveClient(LoanApplicationRequestDto loanApplicationRequestDto) {

        if (clientRepository.existsByEmail(loanApplicationRequestDto.getEmail())) {

            throw new UserAlreadyExistException("Такой пользователь уже существует");
        }

        var passport = passportService.getPassport(loanApplicationRequestDto);
        var client = getClient(loanApplicationRequestDto);

        client.setPassport(passport);

        passportService.savePassport(passport);


        clientRepository.save(client);

        return client;
    }

    public Client getClient(LoanApplicationRequestDto loanApplicationRequestDto) {
        return clientMapper.loanApplicationRequestDtoToModelClient(loanApplicationRequestDto);
    }
}
