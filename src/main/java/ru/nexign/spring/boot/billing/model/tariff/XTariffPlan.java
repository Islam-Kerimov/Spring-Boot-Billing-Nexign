package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

import java.time.LocalTime;

public class XTariffPlan extends Tariff implements TariffPlan {

    private final ByMinuteTariffPlan redirectTariffPlan;

    public XTariffPlan() {
        redirectTariffPlan = new ByMinuteTariffPlan();
    }
    @Override
    public double addCostCall(LocalTime duration) {
        if (incomingInside || outgoingInside) {
            return 0;
        }

        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }
        return minutes * redirectTariffPlan.getMinutePrice();
    }
}
