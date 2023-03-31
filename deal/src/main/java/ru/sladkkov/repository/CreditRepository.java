package ru.sladkkov.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.sladkkov.model.Credit;

public interface CreditRepository extends JpaRepository<Credit, UUID> {}
