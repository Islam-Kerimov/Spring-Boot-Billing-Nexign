package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.dto.NewSubscriberRequest;
import ru.nexign.spring.boot.billing.model.entity.Operator;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.OperatorRepository;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.util.List;
import java.util.Optional;

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

    public Subscriber createSubscriber(NewSubscriberRequest request) {
        Optional<Tariff> byUuid = tariffRepository.findByUuid(request.getTariffUuid());
        Optional<Operator> byName = operatorRepository.findByName(request.getOperator());

        if (byUuid.isEmpty()) {
            throw new RuntimeException("Такого тарифа не существует");
        }
        if (byName.isEmpty()) {
            throw new RuntimeException("Такого оператора не существует");
        }

        Subscriber subscriber = Subscriber.builder()
                .phoneNumber(request.getPhoneNumber())
                .tariff(byUuid.get())
                .balance(request.getBalance())
                .operator(byName.get())
                .build();
        log.info("Create new subscriber {}", subscriber);
        return subscriberRepository.save(subscriber);
    }

    public Optional<Subscriber> getSubscriber(String phoneNumber) {
        return subscriberRepository.findByPhoneNumber(phoneNumber);
    }
}
