package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.time.LocalTime;

public class OrdinaryTariffPlan extends Tariff implements TariffPlan {
    private final ByMinuteTariffPlan redirectTariffPlan;

    public OrdinaryTariffPlan() {
        redirectTariffPlan = new ByMinuteTariffPlan();
    }

    @Override
    public double addCostCall(LocalTime duration) {
        if (incomingAnother || incomingInside) {
            return 0;
        }

        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }

        if (firstMin > 0) {
            double totalCost;
            if (minutes <= firstMin) {
                totalCost = (minutes * firstPrice);
                firstMin -= minutes;
            } else {
                totalCost = (firstMin * firstPrice) + ((minutes - firstMin) * redirectTariffPlan.getMinutePrice());
                firstMin = 0;
            }
            return totalCost;
        } else {
            return minutes * redirectTariffPlan.getMinutePrice();
        }
    }
}
