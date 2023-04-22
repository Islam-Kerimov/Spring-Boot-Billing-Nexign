package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {

    Optional<Tariff> findByUuid(String uuid);

    Boolean existsByUuid(String uuid);

    Boolean existsByUuidOrName(String uuid, String name);
}
