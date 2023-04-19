package ru.nexign.spring.boot.billing.model.mapper;

import org.mapstruct.Mapper;
import ru.nexign.spring.boot.billing.model.domain.ByMinuteTariff;
import ru.nexign.spring.boot.billing.model.domain.OrdinaryTariff;
import ru.nexign.spring.boot.billing.model.domain.UnlimitedTariff;
import ru.nexign.spring.boot.billing.model.domain.XTariff;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

@Mapper(componentModel = "spring")
public abstract class TariffPlanMapper {

	public ByMinuteTariff tariffToByMinuteTariff(Tariff tariff) {
		if (tariff == null) {
			return null;
		}

		return ByMinuteTariff.builder()
			.minutePrice(tariff.getMinutePrice())
			.build();
	}

	public UnlimitedTariff tariffToUnlimitedTariff(Tariff tariff) {
		if (tariff == null) {
			return null;
		}

		return UnlimitedTariff.builder()
			.fixMin(tariff.getFixMin())
			.fixPrice(tariff.getFixPrice())
			.minutePrice(tariff.getMinutePrice())
			.build();
	}

	public OrdinaryTariff tariffToOrdinaryTariff(Tariff tariff, Tariff byMinute) {
		if (tariff == null) {
			return null;
		}

		return OrdinaryTariff.builder()
			.incomingAnother(tariff.getIncomingAnother())
			.incomingInside(tariff.getIncomingInside())
			.firstMin(tariff.getFirstMin())
			.firstPrice(tariff.getFirstPrice())
			.operator(tariff.getOperator())
			.byMinuteTariff(ByMinuteTariff.builder()
				.minutePrice(byMinute.getMinutePrice())
				.build())
			.build();
	}

	public XTariff tariffToXTariff(Tariff tariff, Tariff byMinute) {
		if (tariff == null) {
			return null;
		}

		return XTariff.builder()
			.incomingInside(tariff.getIncomingInside())
			.outgoingInside(tariff.getOutgoingInside())
			.operator(tariff.getOperator())
			.byMinuteTariff(ByMinuteTariff.builder()
				.minutePrice(byMinute.getMinutePrice())
				.build())
			.build();
	}
}
