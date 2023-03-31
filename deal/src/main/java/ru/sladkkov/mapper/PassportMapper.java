package ru.sladkkov.mapper;

import org.mapstruct.Mapper;
import ru.sladkkov.dto.PassportDto;
import ru.sladkkov.model.Passport;

@Mapper(componentModel = "spring")
public interface PassportMapper {
  PassportDto toDto(Passport passport);

  Passport toModel(PassportDto passportDto);
}
