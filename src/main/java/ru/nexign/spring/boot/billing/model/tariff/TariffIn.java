package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.Duration;
import java.time.LocalTime;

public interface TariffIn {
    double addCostCall(CallType callType, LocalTime duration);
}
