package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.LocalTime;

public interface TariffPlan {
    double addCostCall(LocalTime duration);
}
