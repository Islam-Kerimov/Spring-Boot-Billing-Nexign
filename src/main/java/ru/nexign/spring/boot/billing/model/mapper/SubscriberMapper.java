package ru.nexign.spring.boot.billing.model.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.nexign.spring.boot.billing.model.dto.PaymentDto;
import ru.nexign.spring.boot.billing.model.dto.ReportResponse;
import ru.nexign.spring.boot.billing.model.dto.SubscriberDto;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.Operator;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.util.List;

/**
 * Маппер объекта Entity в DTO и обратно.
 */
@Mapper(componentModel = "spring", uses = BillingReportMapper.class)
public abstract class SubscriberMapper {

	public Subscriber subscriberDtoToSubscriber(SubscriberDto subscriberDto) {
		if (subscriberDto == null) {
			return null;
		}

		Subscriber.SubscriberBuilder subscriber = Subscriber.builder();

		subscriber.phoneNumber(subscriberDto.getPhoneNumber());
		subscriber.tariff(Tariff.builder().uuid(subscriberDto.getTariffUuid()).build());
		subscriber.balance(subscriberDto.getBalance());
		subscriber.operator(Operator.builder().name(subscriberDto.getOperator()).build());

		return subscriber.build();
	}

	@Named("subscriber")
	@Mapping(target = "tariffUuid", source = "subscriber.tariff.uuid")
	@Mapping(target = "operator", source = "subscriber.operator.name")
	public abstract SubscriberDto subscriberToSubscriberDto(Subscriber subscriber);

	@IterableMapping(qualifiedByName = "subscriber")
	public abstract List<SubscriberDto> subscriberListToSubscriberDtoList(List<Subscriber> subscribers);

	@Mapping(target = "tariffUuid", source = "subscriber.tariff.uuid")
	@Mapping(target = "operator", source = "subscriber.operator.name")
	@Mapping(target = "monetaryUnit", source = "subscriber.tariff.monetaryUnit")
	@Mapping(target = "payload", source = "billingReports")
	@Mapping(target = "totalCost", source = "totalCost")
	public abstract ReportResponse subscriberBillingToReportResponse(Subscriber subscriber, List<BillingReport> billingReports, Double totalCost);

	@Mapping(target = "balance", source = "money")
	public abstract Subscriber paymentDtoToSubscriber(PaymentDto paymentDto);

	@Mapping(target = "money", source = "balance")
	public abstract PaymentDto subscriberToPaymentDto(Subscriber subscriber);
}

