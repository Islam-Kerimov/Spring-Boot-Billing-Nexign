package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.time.LocalTime;

public class ByMinuteTariffPlan extends Tariff implements TariffPlan {

    @Override
    public double addCostCall(LocalTime duration) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }
        return minutes * minutePrice;
    }
}
