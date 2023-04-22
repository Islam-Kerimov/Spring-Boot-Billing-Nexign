package ru.nexign.spring.boot.billing.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.repository.OperatorRepository;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

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

    public Optional<Subscriber> updateTariff(Subscriber subscriber) {
        return subscriberRepository.findByPhoneNumber(subscriber.getPhoneNumber())
                .map(entity -> {
                    Optional<Tariff> byUuid = tariffRepository.findByUuid(subscriber.getTariff().getUuid());
                    if (byUuid.isPresent()) {
                        log.info("Updating tariff {} on {} by phone_number {}", byUuid.get().getUuid(),
                                subscriber.getTariff().getUuid(), subscriber.getPhoneNumber());
                        entity.setTariff(byUuid.get());
                        subscriberRepository.saveAndFlush(entity);
                        return entity;
                    }
                    throw new EntityNotFoundException(format("tariff_id %s not found", subscriber.getTariff().getUuid()));
                });
    }

    public Optional<Subscriber> updateBalance(Subscriber subscriber) {
        return subscriberRepository.findByPhoneNumber(subscriber.getPhoneNumber())
                .map(entity -> {
                    log.info("Replenishment of the balance on '{}' of the phone_number {}", subscriber.getBalance(), subscriber.getPhoneNumber());
                    entity.setBalance(entity.getBalance() + subscriber.getBalance());
                    subscriberRepository.saveAndFlush(entity);
                    return entity;
                });
    }

    @Transactional
    public Optional<Subscriber> createSubscriber(Subscriber subscriber) {
        if (!tariffRepository.existsByUuid(subscriber.getTariff().getUuid())) {
            throw new EntityNotFoundException(format("tariff_id %s not found", subscriber.getTariff().getUuid()));
        }
        if (!operatorRepository.existsByName(subscriber.getOperator().getName())) {
            throw new EntityNotFoundException(format("operator %s not found", subscriber.getOperator().getName()));
        }
        if (!subscriberRepository.existsByPhoneNumber(subscriber.getPhoneNumber())) {

            int isSave = subscriberRepository.saveBy(
                    subscriber.getPhoneNumber(),
                    subscriber.getTariff().getUuid(),
                    subscriber.getBalance(),
                    subscriber.getOperator().getName());
            if (isSave > 0) {
                return getSubscriber(subscriber.getPhoneNumber());
            }
        }
        return Optional.empty();
    }

    public Optional<Subscriber> getSubscriber(String phoneNumber) {
        return subscriberRepository.findByPhoneNumber(phoneNumber);
    }

    public Set<Subscriber> getAllBillingSubscribers(Set<String> phoneNumbers) {
        return subscriberRepository.findAllByPhoneNumberIn(phoneNumbers);
    }
}
