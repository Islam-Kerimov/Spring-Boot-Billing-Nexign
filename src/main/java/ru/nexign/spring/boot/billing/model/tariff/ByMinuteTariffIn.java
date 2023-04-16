package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.Duration;
import java.time.LocalTime;

public class ByMinuteTariffIn implements TariffIn {
    public static final double minRub = 1.5;

    @Override
    public double addCostCall(CallType callType, LocalTime duration) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }
        return minutes * minRub;
    }
}
