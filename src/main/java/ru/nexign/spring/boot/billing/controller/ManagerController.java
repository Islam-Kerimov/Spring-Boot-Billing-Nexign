package ru.nexign.spring.boot.billing.controller;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nexign.spring.boot.billing.model.dto.*;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.model.mapper.SubscriberMapper;
import ru.nexign.spring.boot.billing.model.mapper.TariffMapper;
import ru.nexign.spring.boot.billing.repository.OperatorRepository;
import ru.nexign.spring.boot.billing.service.*;

import java.util.*;

import static java.lang.String.format;

@RestController
@RequestMapping(value = "/api/v1/manager")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ManagerController {
    private final OperatorRepository operatorRepository;
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
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriberDto createSubscriber(
            @Validated({Marker.OnCreate.class})
            @RequestBody SubscriberDto request) {
        Optional<Subscriber> subscriber = subscriberService.createSubscriber(subscriberMapper.subscriberDtoToSubscriber(request));
        return subscriber.map(subscriberMapper::subscriberToSubscriberDto)
                .orElseThrow(() -> new EntityExistsException(format("entity with phone number %s exist", request.getPhoneNumber())));
    }

    @PostMapping("/tariff")
    @ResponseStatus(HttpStatus.CREATED)
    public TariffDto createTariff(
            @Validated({Marker.OnCreate.class})
            @RequestBody TariffDto request) {
        Optional<Tariff> tariff = tariffService.createTariff(tariffMapper.tariffDtoToTariff(request));
        return tariff.map(tariffMapper::tariffToTariffDto)
                .orElseThrow(() -> new EntityExistsException(format("tariff with uuid %s or name %s exist", request.getUuid(), request.getName())));
    }

    @Transactional
    @PatchMapping("/billing")
    public BillingResponse getAllCurrencies(
            @Validated @RequestBody BillingRequest request,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
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
        List<Sort.Order> orders = getOrders(sort);
        Set<Subscriber> subscribers = subscriberService.getAllBillingSubscribers(totalCost.keySet(), Sort.by(orders));
        return BillingResponse.builder()
                .numbers(subscriberMapper.subscriberListToSubscriberDtoList(subscribers.stream().toList()))
                .build();
    }

    @Transactional
    @GetMapping("/abonents")
    public BillingResponse getAllSubscribers(@RequestParam(defaultValue = "id,asc") String[] sort) {
        List<Sort.Order> orders = getOrders(sort);
        List<Subscriber> subscribers = subscriberService.getAllSubscribers(Sort.by(orders));
        return BillingResponse.builder()
                .numbers(subscriberMapper.subscriberListToSubscriberDtoList(subscribers))
                .build();
    }

    private List<Sort.Order> getOrders(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] sortValue = sortOrder.split(",");
                orders.add(new Sort.Order(
                        sortValue.length == 2
                                ? getSortDirection(sortValue[1])
                                : Sort.Direction.ASC,
                        sortValue[0]));
            }
        } else {
            orders.add(new Sort.Order(
                    sort.length == 2
                            ? getSortDirection(sort[1])
                            : Sort.Direction.ASC,
                    sort[0]));
        }

        return orders;
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
}
