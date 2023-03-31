package ru.sladkkov.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanOfferDtoList {
  @JsonProperty("loanOfferDtoList") private List<LoanOfferDto> loanOfferDtoListWrapper;
}
