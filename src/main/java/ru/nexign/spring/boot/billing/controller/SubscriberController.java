package ru.nexign.spring.boot.billing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
import ru.nexign.spring.boot.billing.service.crm.BillingReportService;
import ru.nexign.spring.boot.billing.service.crm.SubscriberService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@RestController
@RequestMapping(value = "/api/v1/abonent")
@Validated
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Abonent", description = "The Abonent API. Contains all operations of subscriber interaction with the system")
public class SubscriberController {
    private final SubscriberService subscriberService;
    private final BillingReportService billingReportService;
    private final SubscriberMapper subscriberMapper;

    @PatchMapping("/pay")
    @Operation(summary = "Пополнение баланса абонентом",
            description = "Абонент пополняет свой счет")
    public PaymentDto updateBalance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "В теле запроса обязательно должен быть номер абонента и сумма, на которую абонент планирует пополнить баланс",
                    required = true)
            @Validated({Marker.OnUpdate.class})
            @RequestBody PaymentDto request) {
        Optional<Subscriber> subscriber = subscriberService.updateBalance(subscriberMapper.paymentDtoToSubscriber(request));
        return subscriber.map(subscriberMapper::subscriberToPaymentDto)
                .orElseThrow(() -> new EntityNotFoundException(format("entity with phone number %s not found", request.getPhoneNumber())));
    }

    @Transactional
    @GetMapping("/balance/{phoneNumber}")
    @Operation(summary = "Получение баланса абонента",
            description = "Абонент получает данные о своем балансе")
    public SubscriberDto getBalance(
            @Pattern(regexp = "^7\\d{10}$", message = "enter a valid phone number")
            @PathVariable String phoneNumber) {
        Optional<Subscriber> byPhoneNumber = subscriberService.getSubscriber(phoneNumber);
        return byPhoneNumber.map(subscriberMapper::subscriberToSubscriberDto)
                .orElseThrow(() -> new EntityNotFoundException(format("entity with phone number %s not found", phoneNumber)));
    }

    @Transactional
    @GetMapping("/report/{phoneNumber}")
    @Operation(summary = "Получение детализации звонка абонента",
            description = "Абонент получает полную детализацию своих звонков")
    public ReportResponse getReport(
            @Pattern(regexp = "^7\\d{10}$", message = "enter a valid phone number")
            @PathVariable String phoneNumber,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        Optional<Subscriber> subscriber = subscriberService.getSubscriber(phoneNumber);
        if (subscriber.isEmpty()) {
            throw new EntityNotFoundException(format("entity with phone number %s not found", phoneNumber));
        }

        Double fixPrice = subscriber.get().getTariff().getFixPrice();
        if (fixPrice == null) {
            fixPrice = 0.0;
        }

        List<Sort.Order> orders = getOrders(sort);
        List<BillingReport> billingReports = billingReportService.getAllBillingReportBy(phoneNumber, Sort.by(orders));
        double totalCost = fixPrice + billingReports.stream()
                .map(BillingReport::getCost)
                .mapToDouble(Double::doubleValue)
                .sum();

        return subscriberMapper.subscriberBillingToReportResponse(subscriber.get(), billingReports, totalCost);
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
