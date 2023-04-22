package ru.nexign.spring.boot.billing.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.Marker;
import ru.nexign.spring.boot.billing.model.dto.PaymentDto;
import ru.nexign.spring.boot.billing.model.dto.ReportResponse;
import ru.nexign.spring.boot.billing.model.dto.SubscriberDto;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.mapper.SubscriberMapper;
import ru.nexign.spring.boot.billing.service.BillingReportService;
import ru.nexign.spring.boot.billing.service.SubscriberService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/abonent")
@Validated
@RequiredArgsConstructor
@Slf4j
public class SubscriberController {
    private final SubscriberService subscriberService;
    private final BillingReportService billingReportService;
    private final SubscriberMapper subscriberMapper;

    @PatchMapping("/pay")
    public PaymentDto getAllCurrencies(
            @Validated({Marker.OnUpdate.class})
            @RequestBody PaymentDto request) {
        Optional<Subscriber> subscriber = subscriberService.updateBalance(subscriberMapper.paymentDtoToSubscriber(request));
        return subscriber.map(subscriberMapper::subscriberToPaymentDto).orElse(null);
    }

    @Transactional
    @GetMapping("/balance/{phoneNumber}")
    public SubscriberDto getBalance(
            @Pattern(regexp = "^7\\d{10}$", message = "enter a valid phone number")
            @PathVariable String phoneNumber) {
        Optional<Subscriber> byPhoneNumber = subscriberService.getSubscriber(phoneNumber);
        return byPhoneNumber.map(subscriberMapper::subscriberToSubscriberDto).orElse(null);
    }

    @Transactional
    @GetMapping("/report/{phoneNumber}")
    public ReportResponse getReport(
            @Pattern(regexp = "^7\\d{10}$", message = "enter a valid phone number")
            @PathVariable String phoneNumber) {
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
