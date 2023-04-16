package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.Operator;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Integer> {

    Optional<Operator> findByName(String name);
}
