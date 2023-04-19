package ru.nexign.spring.boot.billing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.SubscriberDto;
import ru.nexign.spring.boot.billing.model.dto.PayRequest;
import ru.nexign.spring.boot.billing.model.dto.PayResponse;
import ru.nexign.spring.boot.billing.model.dto.ReportResponse;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.mapper.SubscriberMapper;
import ru.nexign.spring.boot.billing.service.BillingReportService;
import ru.nexign.spring.boot.billing.service.SubscriberService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/abonent")
@RequiredArgsConstructor
@Slf4j
public class SubscriberController {
	private final SubscriberService subscriberService;
	private final BillingReportService billingReportService;
	private final SubscriberMapper subscriberMapper;

	@PatchMapping("/pay")
	public PayResponse getAllCurrencies(@RequestBody PayRequest request) {
		Optional<Subscriber> subscriber = subscriberService.updateBalance(subscriberMapper.payRequestToSubscriber(request));
		return subscriber.map(subscriberMapper::subscriberToPayResponse).orElse(null);
	}

	@GetMapping("/balance/{phoneNumber}")
	public SubscriberDto getBalance(@PathVariable String phoneNumber) {
		Optional<Subscriber> byPhoneNumber = subscriberService.getSubscriber(phoneNumber);
		return byPhoneNumber.map(subscriberMapper::subscriberToSubscriberDto).orElse(null);
	}

	@Transactional
	@GetMapping("/report/{phoneNumber}")
	public ReportResponse getReport(@PathVariable String phoneNumber) {
		Optional<Subscriber> subscriber = subscriberService.getSubscriber(phoneNumber);
		if (subscriber.isPresent()) {
			Double fixPrice = subscriber.get().getTariff().getFixPrice();
			if (fixPrice == null) {
				fixPrice = 0.0;
			}

			List<BillingReport> billingReports = billingReportService.getAllBillingReportBy(phoneNumber);
			double totalCost = fixPrice + billingReports.stream()
				.map(BillingReport::getCost)
				.mapToDouble(Double::doubleValue)
				.sum();

			return subscriberMapper.subscriberBillingToReportResponse(subscriber.get(), billingReports, totalCost);
		}
		return null;
	}
}
