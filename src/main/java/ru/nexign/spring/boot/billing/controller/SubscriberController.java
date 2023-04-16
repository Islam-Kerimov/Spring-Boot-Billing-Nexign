package ru.nexign.spring.boot.billing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.*;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
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

    @PatchMapping("/pay")
    public PayResponse getAllCurrencies(@RequestBody PayRequest request) {
        Optional<Subscriber> subscriber = subscriberService.updateBalance(request.getPhoneNumber(), request.getBalance());

        return subscriber.map(value -> PayResponse.builder()
                .id(value.getId())
                .phoneNumber(value.getPhoneNumber())
                .balance(value.getBalance())
                .build()).orElse(null);
    }

    @GetMapping("/balance/{phoneNumber}")
    public SubscriberDto getBalance(@PathVariable String phoneNumber) {
        Optional<Subscriber> byPhoneNumber = subscriberService.getSubscriber(phoneNumber);
        return byPhoneNumber.map(subscriber -> SubscriberDto.builder()
                .phoneNumber(subscriber.getPhoneNumber())
                .balance(subscriber.getBalance())
                .build()).orElse(null);
    }

    @Transactional
    @GetMapping("/report/{phoneNumber}")
    public ReportResponse getReport(@PathVariable String phoneNumber) {
        Optional<Subscriber> subscriber = subscriberService.getSubscriber(phoneNumber);

        if (subscriber.isPresent()) {
            List<BillingReport> billingReport = billingReportService.getPayload(phoneNumber);
            List<BillingReportDto> billingReportDtos = billingReport.stream()
                    .map(v -> BillingReportDto.builder()
                            .callType(v.getCallType())
                            .startTime(v.getCallStart())
                            .endTime(v.getCallEnd())
                            .duration(v.getDuration())
                            .cost(v.getCost())
                            .build())
                    .toList();
            return ReportResponse.builder()
                    .id(subscriber.get().getId())
                    .phoneNumber(subscriber.get().getPhoneNumber())
                    .tariffUuid(subscriber.get().getTariff().getUuid())
                    .operator(subscriber.get().getOperator().getName())
                    .payload(billingReportDtos)
                    .totalCost(billingReport.stream()
                            .map(BillingReport::getCost)
                            .mapToDouble(Double::doubleValue)
                            .sum())
                    .monetaryUnit(subscriber.get().getTariff().getMonetaryUnit())
                    .build();
        }
        return null;
    }
}
