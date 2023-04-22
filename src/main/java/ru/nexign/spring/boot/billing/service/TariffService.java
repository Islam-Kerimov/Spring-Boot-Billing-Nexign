package ru.nexign.spring.boot.billing.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.OperatorRepository;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {
    private final TariffRepository tariffRepository;
    private final OperatorRepository operatorRepository;

    public Optional<Tariff> createTariff(Tariff tariff) {
        if (!operatorRepository.existsByName(tariff.getOperator())) {
            throw new EntityNotFoundException(String.format("operator %s not found", tariff.getOperator()));
        }
        if (!tariffRepository.existsByUuidOrName(tariff.getUuid(), tariff.getName())) {
            return Optional.of(tariffRepository.save(tariff));
        }
        return Optional.empty();
    }
}
