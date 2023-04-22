package ru.nexign.spring.boot.billing.controller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.*;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.model.mapper.SubscriberMapper;
import ru.nexign.spring.boot.billing.model.mapper.TariffMapper;
import ru.nexign.spring.boot.billing.service.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@RestController
@RequestMapping(value = "/manager")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ManagerController {
    private final BillingRealTimeService billingRealTimeService;
    private final HighPerformanceRatingServerService highPerformanceRatingServerService;
    private final SubscriberService subscriberService;
    private final TariffService tariffService;
    private final SubscriberMapper subscriberMapper;
    private final TariffMapper tariffMapper;
    private final GeneratorCallDataService generator;


    @Transactional
    @PatchMapping("/changeTariff")
    public SubscriberDto updateTariff(
            @Validated({Marker.OnUpdate.class})
            @RequestBody SubscriberDto request) {
        Optional<Subscriber> subscriber = subscriberService.updateTariff(subscriberMapper.subscriberDtoToSubscriber(request));
        return subscriber.map(subscriberMapper::subscriberToSubscriberDto)
                .orElseThrow(() -> new EntityNotFoundException(format("entity with phone number %s not found", request.getPhoneNumber())));
    }

    @Transactional
    @PostMapping("/abonent")
    public SubscriberDto createSubscriber(
            @Validated({Marker.OnCreate.class})
            @RequestBody SubscriberDto request) {
        Optional<Subscriber> subscriber = subscriberService.createSubscriber(subscriberMapper.subscriberDtoToSubscriber(request));
        return subscriber.map(subscriberMapper::subscriberToSubscriberDto)
                .orElseThrow(() -> new EntityExistsException(format("entity with phone number %s exist", request.getPhoneNumber())));
    }

    @PostMapping("/tariff")
    public TariffDto createTariff(
            @Validated({Marker.OnCreate.class})
            @RequestBody TariffDto request) {
        Optional<Tariff> tariff = tariffService.createTariff(tariffMapper.tariffDtoToTariff(request));
        return tariff.map(tariffMapper::tariffToTariffDto)
                .orElseThrow(() -> new EntityExistsException(format("tariff with uuid %s or name %s exist", request.getUuid(), request.getName())));
    }

    @Transactional
    @PatchMapping("/billing")
    public BillingResponse getAllCurrencies(@Validated @RequestBody BillingRequest request) {
        if (!request.getAction().equalsIgnoreCase("run")) {
            throw new RuntimeException("action must be 'run'");
        }

        // генерация новых данных
        generator.generate();

        // тарификация
        String cdrPlusFile = billingRealTimeService.billing();
        Map<String, Double> totalCost = highPerformanceRatingServerService.computeSubscriberTotalCost(cdrPlusFile);
        billingRealTimeService.updateBalance(totalCost);

        // создание респонса
        Set<Subscriber> subscribers = subscriberService.getAllBillingSubscribers(totalCost.keySet());
        return BillingResponse.builder()
                .numbers(subscriberMapper.subscriberListToSubscriberDtoList(subscribers.stream().toList()))
                .build();
    }

    @Transactional
    @GetMapping("/abonents")
    public BillingResponse getAllSubscribers() {
        List<Subscriber> subscribers = subscriberService.getAllSubscribers();
        return BillingResponse.builder()
                .numbers(subscriberMapper.subscriberListToSubscriberDtoList(subscribers))
                .build();
    }
}
