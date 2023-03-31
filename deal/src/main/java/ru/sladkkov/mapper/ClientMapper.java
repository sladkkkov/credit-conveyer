package ru.sladkkov.mapper;

import org.mapstruct.*;
import ru.sladkkov.dto.LoanApplicationRequestDto;
import ru.sladkkov.model.Client;
import ru.sladkkov.model.Credit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = PassportMapper.class)
public interface ClientMapper {

  default Client loanApplicationRequestDtoToModelClient(
      LoanApplicationRequestDto loanApplicationRequestDto) {
    return Client.builder()
        .firstName(loanApplicationRequestDto.getFirstName())
        .middleName(loanApplicationRequestDto.getMiddleName())
        .lastName(loanApplicationRequestDto.getLastName())
        .birthdate(loanApplicationRequestDto.getBirthday())
        .email(loanApplicationRequestDto.getEmail())
        .build();
  }

  default LoanApplicationRequestDto clientToLoanApplicationRequestDto(
      Client client) {
    return LoanApplicationRequestDto.builder()
        .firstName(client.getFirstName())
        .middleName(client.getMiddleName())
        .lastName(client.getLastName())
        .email(client.getEmail())
        .birthday(client.getBirthdate())
        .email(client.getEmail())
        .build();
  }
}
