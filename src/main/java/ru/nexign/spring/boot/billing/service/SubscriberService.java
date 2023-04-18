package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nexign.spring.boot.billing.model.dto.NewSubscriberRequest;
import ru.nexign.spring.boot.billing.model.entity.Operator;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.OperatorRepository;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

	private final SubscriberRepository subscriberRepository;
	private final TariffRepository tariffRepository;
	private final OperatorRepository operatorRepository;

	public List<Subscriber> getAllSubscribers() {
		return subscriberRepository.findAll();
	}

	public Optional<Subscriber> updateTariff(String phoneNumber, String newTariffUuid) {
		return subscriberRepository.findByPhoneNumber(phoneNumber)
			.map(entity -> {
				Optional<Tariff> byUuid = tariffRepository.findByUuid(newTariffUuid);
				if (byUuid.isPresent()) {
					log.info("Updating tariff {} on {} by phone_number {}", byUuid.get().getUuid(), newTariffUuid, phoneNumber);
					entity.setTariff(byUuid.get());
					subscriberRepository.saveAndFlush(entity);
					return entity;
				}
				return null;
			});
	}

	public Optional<Subscriber> updateBalance(String phoneNumber, Double balance) {
		return subscriberRepository.findByPhoneNumber(phoneNumber)
			.map(entity -> {
				log.info("Replenishment of the balance on '{}' of the phone_number {}", balance, phoneNumber);
				entity.setBalance(entity.getBalance() + balance);
				subscriberRepository.saveAndFlush(entity);
				return entity;
			});
	}

	@Transactional
	public int createSubscriber(NewSubscriberRequest request) {
		if (!subscriberRepository.existsByPhoneNumber(request.getPhoneNumber()) &&
			tariffRepository.existsByUuid(request.getTariffUuid()) &&
			operatorRepository.existsByName(request.getOperator())) {

			return subscriberRepository.saveBy(request.getPhoneNumber(), request.getTariffUuid(), request.getBalance(), request.getOperator());
		}
		return 0;
	}

	public Optional<Subscriber> getSubscriber(String phoneNumber) {
		return subscriberRepository.findByPhoneNumber(phoneNumber);
	}

	public Set<Subscriber> getAllBillingSubscribers(Set<String> phoneNumbers) {
		return subscriberRepository.findAllByPhoneNumberIn(phoneNumbers);
	}
}
