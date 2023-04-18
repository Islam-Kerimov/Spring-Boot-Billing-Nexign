package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.dto.NewTariffRequest;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TariffService {
	private final TariffRepository tariffRepository;

	public Tariff createTariff(NewTariffRequest request) {
		if (!tariffRepository.existsByUuid(request.getUuid())) {

			return tariffRepository.save(Tariff.builder()
				.uuid(request.getUuid())
				.name(request.getName())
				.fixMin(request.getFixMin())
				.fixPrice(request.getFixPrice())
				.firstMin(request.getFirstMin())
				.firstPrice(request.getFirstPrice())
				.minutePrice(request.getMinutePrice())
				.incomingInside(request.getIncomingInside())
				.outgoingInside(request.getOutgoingInside())
				.incomingAnother(request.getIncomingAnother())
				.outgoingAnother(request.getOutgoingAnother())
				.monetaryUnit(request.getMonetaryUnit())
				.redirect(request.getRedirect())
				.operator(request.getOperator())
				.build()
			);
		}
		return null;
	}
}
