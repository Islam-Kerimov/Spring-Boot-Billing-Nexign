package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.Duration;
import java.time.LocalTime;

public class XTariffIn implements TariffIn {
    @Override
    public double addCostCall(CallType callType, LocalTime duration) {
        return 0;
    }
}
