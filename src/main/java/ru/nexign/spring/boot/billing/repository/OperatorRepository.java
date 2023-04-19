package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.Operator;

public interface OperatorRepository extends JpaRepository<Operator, Integer> {

	Boolean existsByName(String name);
}
