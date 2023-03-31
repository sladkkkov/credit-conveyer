package ru.sladkkov.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sladkkov.model.Passport;

@Repository
public interface PassportRepository extends JpaRepository<Passport, UUID> {}
