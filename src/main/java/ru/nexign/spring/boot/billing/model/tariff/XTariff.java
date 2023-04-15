package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.CallType;

import java.time.LocalTime;

public class XTariff implements Tariff {
    @Override
    public double addCostCall(CallType callType, LocalTime duration) {
        return 0;
    }
}
