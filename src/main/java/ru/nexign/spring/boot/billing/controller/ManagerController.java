package ru.nexign.spring.boot.billing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.*;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;
import ru.nexign.spring.boot.billing.service.BillingRealTimeService;
import ru.nexign.spring.boot.billing.service.HighPerformanceRatingServerService;
import ru.nexign.spring.boot.billing.service.SubscriberService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @PostMapping("/abonent")
    public NewSubscriberResponse createSubscriber(@RequestBody NewSubscriberRequest request) {
        Subscriber subscriber = subscriberService.createSubscriber(request);
        return NewSubscriberResponse.builder()
                .id(subscriber.getId())
                .phoneNumber(subscriber.getPhoneNumber())
                .tariffUuid(subscriber.getTariff().getUuid())
                .balance(subscriber.getBalance())
                .operator(subscriber.getOperator().getName())
                .build();
    }

    @PatchMapping("/billing")
    public BillingResponse getAllCurrencies(@RequestBody BillingRequest request) {
        if (request.getAction().equals("run")) {
            // тарификация
            String cdrPlusFile = billingRealTimeService.billing();
            Map<String, Double> totalCost = highPerformanceRatingServerService.computeSubscriberTotalCost(cdrPlusFile);
            billingRealTimeService.updateBalance(totalCost);

            // создание респонса
            List<Subscriber> subscribers = subscriberService.getAllSubscribers();
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
