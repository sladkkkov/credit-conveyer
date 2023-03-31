package ru.sladkkov.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.mapper.PassportMapper;
import ru.sladkkov.model.Passport;
import ru.sladkkov.repository.PassportRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportService {

  private final PassportRepository passportRepository;
  private final PassportMapper passportMapper;

  public Passport savePassport(Passport passport) {
    return passportRepository.save(passport);
  }


  public Passport getPassport(LoanApplicationRequestDto loanApplicationRequestDto) {
    return passportMapper.toModel(loanApplicationRequestDto.getPassportDto());}
}
