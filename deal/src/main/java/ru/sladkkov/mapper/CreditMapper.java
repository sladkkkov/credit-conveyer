package ru.sladkkov.mapper;

import org.mapstruct.Mapper;
import ru.sladkkov.dto.CreditDto;
import ru.sladkkov.model.Credit;

@Mapper(componentModel = "spring")
public interface CreditMapper {
  CreditDto toDto(Credit credit);

  Credit toModel(CreditDto creditDto);
}
