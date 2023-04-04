package ru.sladkkov.mapper;

import org.mapstruct.Mapper;
import ru.sladkkov.dto.LoanOfferDto;
import ru.sladkkov.model.LoanOffer;

@Mapper(componentModel = "spring")
public interface LoanOfferDtoMapper {
    LoanOfferDto toDto(LoanOffer loanOffer);

    LoanOffer toModel(LoanOfferDto loanOfferDto);
}
