package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {
	private final TariffRepository tariffRepository;

	public Tariff createTariff(Tariff tariff) {
		if (!tariffRepository.existsByUuid(tariff.getUuid())) {
			return tariffRepository.save(tariff);
		}
		return null;
	}
}
