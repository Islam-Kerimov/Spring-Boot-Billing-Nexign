package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.CallType;

import java.time.LocalTime;

public interface Tariff {
    double addCostCall(CallType callType, LocalTime duration);
}
