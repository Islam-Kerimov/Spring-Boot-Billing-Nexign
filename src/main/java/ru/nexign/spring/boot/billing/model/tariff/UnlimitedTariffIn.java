package ru.nexign.spring.boot.billing.model.tariff;

import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.Duration;
import java.time.LocalTime;

public class UnlimitedTariffIn implements TariffIn {
    private static final double minRub = 1.0;
    private int unlimitedMinutes = 300;

    @Override
    public double addCostCall(CallType callType, LocalTime duration) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }

        if (unlimitedMinutes > 0) {
            double totalCost = unlimitedMinutes == 300 ? 100 : 0;
            if (minutes <= unlimitedMinutes) {
                unlimitedMinutes -= minutes;
            } else {
                totalCost += ((minutes - unlimitedMinutes) * minRub);
                unlimitedMinutes = 0;
            }
            return totalCost;
        } else {
            return minutes * minRub;
        }
    }
}
