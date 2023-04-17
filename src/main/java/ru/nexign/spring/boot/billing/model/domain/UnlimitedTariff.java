package ru.nexign.spring.boot.billing.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnlimitedTariff implements TariffPlan {
    private int fixMin;
    private double fixPrice;
    private double minutePrice;

    @Override
    public double addCost(LocalTime duration, CallType callType, Boolean operator) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }

        if (fixMin > 0) {
            double totalCost = 0;
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
