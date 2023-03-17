package ru.sladkkov.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import ru.sladkkov.dto.LoanApplicationRequest;

class ConveyorServiceTest {

  private LoanApplicationRequest loanApplicationRequest =
      LoanApplicationRequest.builder()
          .amount(BigDecimal.ONE)
          .term(35)
          .firstName("Danila")
          .lastName("Sladkkov")
          .middleName("Alekseevich")
          .email("sladkkov@yandex.ru")
          .birthday(LocalDate.parse("06-06-2001", DateTimeFormatter.ofPattern("dd-MM-yyyy"))). ("1234")
          .passportNumber("123456")
          .build();

  @Test
  void onNullLoanApplicationRequestDtoInMethodCalculationService() {
    ConveyorService conveyorService = spy(ConveyorService.class);

    assertThatRuntimeException().isThrownBy(() -> conveyorService.calculationOffers(null));
  }

  @Test
  void onNullLoanApplicationRequestDtoInMethodCreateLoanOfferDTO() {
    ConveyorService conveyorService = spy(ConveyorService.class);

    assertThatRuntimeException()
        .isThrownBy(() -> conveyorService.createLoanOfferDto(null, false, false))
        .withMessage("LoanApplicationRequestDTO is null");
  }

  @Test
  void givenAllTrueFalseCombination() {}

  @Test
  void calculateMonthlyPayment() {}
}
