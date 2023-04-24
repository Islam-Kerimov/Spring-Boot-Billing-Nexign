package ru.nexign.spring.boot.billing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.nexign.spring.boot.billing.service.brt.BillingRealTimeService;
import ru.nexign.spring.boot.billing.service.cdr.GeneratorCallDataService;
import ru.nexign.spring.boot.billing.service.crm.SubscriberService;
import ru.nexign.spring.boot.billing.service.crm.TariffService;
import ru.nexign.spring.boot.billing.service.hrs.HighPerformanceRatingServerService;

import java.util.*;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(value = "/api/v1/manager")
@Validated
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Manager", description = "The Manager API. Contains all operations of manager interaction with the system")
public class ManagerController {

	private static final String ACTION = "run";

	/** Количество переданных параметров для сортировки. */
	private static final Integer SORT_VALUES = 2;

	/** Сортировка по возрастанию. */
	private static final String SORT_ASC = "asc";

	/** Сортировка по убыванию. */
	private static final String SORT_DESC = "desc";

	private final BillingRealTimeService billingRealTimeService;

	private final HighPerformanceRatingServerService highPerformanceRatingServerService;

	private final SubscriberService subscriberService;

	private final TariffService tariffService;

	private final SubscriberMapper subscriberMapper;

	private final TariffMapper tariffMapper;

	private final GeneratorCallDataService generator;

	@Transactional
	@PatchMapping("/changeTariff")
	@Operation(summary = "Смена тарифа абонента",
		description = "Менеджер меняет тариф абонента")
	public SubscriberDto updateTariff(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "В теле запроса обязательно должен быть номер абонента и тариф, на который менеджер планирует изменить у абонента",
			required = true)
		@Validated({Marker.OnUpdate.class})
		@RequestBody SubscriberDto request) {
		Optional<Subscriber> subscriber = subscriberService.updateTariff(subscriberMapper.subscriberDtoToSubscriber(request));
		return subscriber.map(subscriberMapper::subscriberToSubscriberDto)
			.orElseThrow(() -> new EntityNotFoundException(format("entity with phone number %s not found", request.getPhoneNumber())));
	}

	@Transactional
	@PostMapping("/abonent")
	@ResponseStatus(CREATED)
	@Operation(summary = "Создание нового абонента",
		description = "Менеджер создает нового абонента в БД")
	public SubscriberDto createSubscriber(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "В теле запроса обязательно должен быть номер абонента, тариф, сумма начального баланса и оператор абонента",
			required = true)
		@Validated({Marker.OnCreate.class})
		@RequestBody SubscriberDto request) {
		Optional<Subscriber> subscriber = subscriberService.createSubscriber(subscriberMapper.subscriberDtoToSubscriber(request));
		return subscriber.map(subscriberMapper::subscriberToSubscriberDto)
			.orElseThrow(() -> new EntityExistsException(format("entity with phone number %s exist", request.getPhoneNumber())));
	}

	@PostMapping("/tariff")
	@ResponseStatus(CREATED)
	@Operation(summary = "Создание нового тарифа",
		description = "Менеджер создает новый тариф в БД")
	public TariffDto createTariff(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "В теле запроса обязательно должен быть двухзначный уникальный идентификатор, имя и оператор создаваемого тарифа",
			required = true)
		@Validated({Marker.OnCreate.class})
		@RequestBody TariffDto request) {
		Optional<Tariff> tariff = tariffService.createTariff(tariffMapper.tariffDtoToTariff(request));
		return tariff.map(tariffMapper::tariffToTariffDto)
			.orElseThrow(() -> new EntityExistsException(format("tariff with uuid %s or name %s exist",
				request.getUuid(), request.getName())));
	}

	@Transactional
	@PatchMapping("/billing")
	@Operation(summary = "Процесс биллинга звонков",
		description = "Менеджер запускает биллинг звонков всех авторизованных абонентов оператора Ромашка с балансом больше нуля")
	public BillingResponse getAllCurrencies(
		@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "В теле запроса обязательно должно быть поле для запуска биллинга, а именно 'action -> run'",
			required = true)
		@Validated @RequestBody BillingRequest request,
		@RequestParam(defaultValue = "id,asc") String[] sort) {
		if (!request.getAction().equalsIgnoreCase(ACTION)) {
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
	@Operation(summary = "Получение всех абонентов",
		description = "Отображение всех абонентов в БД")
	public BillingResponse getAllSubscribers(@RequestParam(defaultValue = "id,asc") String[] sort) {
		List<Sort.Order> orders = getOrders(sort);
		List<Subscriber> subscribers = subscriberService.getAllSubscribers(Sort.by(orders));
		return BillingResponse.builder()
			.numbers(subscriberMapper.subscriberListToSubscriberDtoList(subscribers))
			.build();
	}

	/**
	 * Создание списка сортировок.
	 *
	 * @param sort полученные параметры сортировки
	 * @return список сортировок
	 */
	private List<Sort.Order> getOrders(String[] sort) {
		List<Sort.Order> orders = new ArrayList<>();
		if (sort[0].contains(",")) {
			for (String sortOrder : sort) {
				String[] sortValue = sortOrder.split(",");
				orders.add(new Sort.Order(
					sortValue.length == SORT_VALUES
						? getSortDirection(sortValue[1])
						: Sort.Direction.ASC,
					sortValue[0]));
			}
		} else {
			orders.add(new Sort.Order(
				sort.length == SORT_VALUES
					? getSortDirection(sort[1])
					: Sort.Direction.ASC,
				sort[0]));
		}

		return orders;
	}

	private Sort.Direction getSortDirection(String direction) {
		if (direction.equalsIgnoreCase(SORT_ASC)) {
			return Sort.Direction.ASC;
		} else if (direction.equalsIgnoreCase(SORT_DESC)) {
			return Sort.Direction.DESC;
		}

		return Sort.Direction.ASC;
	}
}
