package ru.nexign.spring.boot.billing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.*;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.service.BillingRealTimeService;
import ru.nexign.spring.boot.billing.service.HighPerformanceRatingServerService;
import ru.nexign.spring.boot.billing.service.SubscriberService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/manager")
@RequiredArgsConstructor
@Slf4j
public class ManagerController {
	private final BillingRealTimeService billingRealTimeService;
	private final HighPerformanceRatingServerService highPerformanceRatingServerService;
	private final SubscriberService subscriberService;
//    private final SubscriberMapper subscriberMapper;


	@PatchMapping("/changeTariff")
	public ChangeTariffResponse updateTariff(@RequestBody ChangeTariffRequest request) {
		Optional<Subscriber> subscriber = subscriberService.updateTariff(request.getPhoneNumber(), request.getTariffUuid());

		return subscriber.map(value -> ChangeTariffResponse.builder()
			.id(value.getId())
			.phoneNumber(value.getPhoneNumber())
			.tariffUuid(value.getTariff().getUuid())
			.build()).orElse(null);
	}

	@Transactional
	@PostMapping("/abonent")
	public NewSubscriberResponse createSubscriber(@RequestBody NewSubscriberRequest request) {
		Optional<Subscriber> subscriber = Optional.empty();
		if (subscriberService.createSubscriber(request) > 0) {
			subscriber = subscriberService.getSubscriber(request.getPhoneNumber());
		}
		return subscriber.map(value -> NewSubscriberResponse.builder()
			.id(value.getId())
			.phoneNumber(value.getPhoneNumber())
			.tariffUuid(value.getTariff().getUuid())
			.balance(value.getBalance())
			.operator(value.getOperator().getName())
			.build()).orElse(null);
	}

	@PatchMapping("/billing")
	public BillingResponse getAllCurrencies(@RequestBody BillingRequest request) {
		if (request.getAction().equals("run")) {
			// тарификация
			String cdrPlusFile = billingRealTimeService.billing();
			Map<String, Double> totalCost = highPerformanceRatingServerService.computeSubscriberTotalCost(cdrPlusFile);
			billingRealTimeService.updateBalance(totalCost);

			// создание респонса
//			return getAllSubscribers();
			Set<Subscriber> subscribers = subscriberService.getAllBillingSubscribers(totalCost.keySet());
			return BillingResponse.builder()
				.numbers(subscribers.stream()
					.map(data -> SubscriberDto.builder()
						.phoneNumber(data.getPhoneNumber())
						.balance(data.getBalance())
						.build())
					.toList())
				.build();
//            return subscriberMapper.entitySubscriberListToDtoList(subscribers);
		}
		return null;
	}

	@GetMapping("/abonents")
	public BillingResponse getAllSubscribers() {
		List<Subscriber> subscribers = subscriberService.getAllSubscribers();
		return BillingResponse.builder()
			.numbers(subscribers.stream()
				.map(data -> SubscriberDto.builder()
					.phoneNumber(data.getPhoneNumber())
					.balance(data.getBalance())
					.build())
				.toList())
			.build();
	}
}
