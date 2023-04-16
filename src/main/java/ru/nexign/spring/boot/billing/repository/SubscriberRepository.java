package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;

import java.util.Optional;
import java.util.Set;

public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {
    @Modifying
    @Query(value = "" +
            "SELECT s.*" +
            "FROM subscriber s JOIN operator o ON s.operator_id = o.id " +
            "JOIN tariff t ON t.id = s.tariff_id " +
            "WHERE s.balance >= 0 AND o.name = 'Ромашка' ",
            nativeQuery = true)
    Set<Subscriber> findAllByBalanceAndOperator();

    Set<Subscriber> findAllByPhoneNumberIn(Set<String> phoneNumbers);

    Optional<Subscriber> findByPhoneNumber(String phoneNumber);
}
