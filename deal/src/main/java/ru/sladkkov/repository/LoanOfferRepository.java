package ru.sladkkov.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sladkkov.model.LoanOffer;

@Repository
public interface LoanOfferRepository extends JpaRepository<LoanOffer, UUID> {}
