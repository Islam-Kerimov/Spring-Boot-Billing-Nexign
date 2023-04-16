package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {

    Optional<Tariff> findByUuid(String uuid);
}
