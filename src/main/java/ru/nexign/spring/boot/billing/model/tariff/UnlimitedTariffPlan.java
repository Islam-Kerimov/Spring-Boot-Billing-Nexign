package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.time.LocalTime;

public class UnlimitedTariffPlan extends Tariff implements TariffPlan {

    @Override
    public double addCostCall(LocalTime duration) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }

        if (fixMin > 0) {
            double totalCost = fixMin == 300 ? 100 : 0;
            if (minutes <= fixMin) {
                fixMin -= minutes;
            } else {
                totalCost += ((minutes - fixMin) * fixPrice);
                fixMin = 0;
            }
            return totalCost;
        } else {
            return minutes * fixPrice;
        }
    }
}
