package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;

import java.util.Set;

public interface BillingRealTimeRepository extends JpaRepository<Subscriber, Integer> {

    @Modifying
    @Query(value = "" +
            "SELECT s.*, t.uuid " +
            "FROM subscriber s JOIN operator o ON s.operator_id = o.id " +
            "JOIN tariff t ON t.id = s.tariff_id " +
            "WHERE s.balance >= 0 AND o.name = 'Ромашка'",
            nativeQuery = true)
    @Transactional
    Set<Subscriber> findAllByBalanceAndOperator();
}
