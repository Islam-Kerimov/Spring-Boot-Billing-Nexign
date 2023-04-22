package ru.nexign.spring.boot.billing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        return subscriber.map(subscriberMapper::subscriberToSubscriberDto).orElse(null);
    }

    @Transactional
    @PostMapping("/abonent")
    public SubscriberDto createSubscriber(
            @Validated({Marker.OnCreate.class})
            @RequestBody SubscriberDto request) {
        Optional<Subscriber> subscriber = subscriberService.createSubscriber(subscriberMapper.subscriberDtoToSubscriber(request));
        return subscriber.map(subscriberMapper::subscriberToSubscriberDto).orElse(null);
    }

    @PostMapping("/tariff")
    public TariffDto createTariff(
            @Validated({Marker.OnCreate.class})
            @RequestBody TariffDto request) {
        Optional<Tariff> tariff = Optional.ofNullable(tariffService.createTariff(tariffMapper.tariffDtoToTariff(request)));
        return tariff.map(tariffMapper::tariffToTariffDto).orElse(null);
    }

    @Transactional
    @PatchMapping("/billing")
    public BillingResponse getAllCurrencies(@Validated @RequestBody BillingRequest request) {
        if (!request.getAction().equalsIgnoreCase("run")) {
//            throw new Exteption()
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
