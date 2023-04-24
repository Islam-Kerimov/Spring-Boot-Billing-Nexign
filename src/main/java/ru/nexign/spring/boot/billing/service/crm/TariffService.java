package ru.nexign.spring.boot.billing.service.crm;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.OperatorRepository;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {

	private final TariffRepository tariffRepository;

	private final OperatorRepository operatorRepository;

	public Optional<Tariff> createTariff(Tariff tariff) {
		if (!operatorRepository.existsByName(tariff.getOperator())) {
			throw new EntityNotFoundException(format("operator %s not found", tariff.getOperator()));
		}
		if (!tariffRepository.existsByUuidOrName(tariff.getUuid(), tariff.getName())) {
			log.info("Новый тариф {} добавлен", tariff);
			return of(tariffRepository.save(tariff));
		}
		return empty();
	}
}
