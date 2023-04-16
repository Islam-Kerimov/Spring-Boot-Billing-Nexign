package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.Duration;
import java.time.LocalTime;

public class OrdinaryTariffIn implements TariffIn {
    private static final double minRub = 0.5;
    private int firstMinutes = 100;

    @Override
    public double addCostCall(CallType callType, LocalTime duration) {
        if (CallType.INCOMING.equals(callType)) {
            return 0;
        }

        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }

        if (firstMinutes > 0) {
            double totalCost;
            if (minutes <= firstMinutes) {
                totalCost = (minutes * minRub);
                firstMinutes -= minutes;
            } else {
                totalCost = (firstMinutes * minRub) + ((minutes - firstMinutes) * ByMinuteTariffIn.minRub);
                firstMinutes = 0;
            }
            return totalCost;
        } else {
            return minutes * ByMinuteTariffIn.minRub;
        }
    }
}
