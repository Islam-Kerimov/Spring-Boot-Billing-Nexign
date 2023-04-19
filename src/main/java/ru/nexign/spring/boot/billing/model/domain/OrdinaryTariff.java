package ru.nexign.spring.boot.billing.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdinaryTariff implements TariffPlan {
    private boolean incomingInside;
    private boolean incomingAnother;
    private int firstMin;
    private double firstPrice;
    private String operator;
    private ByMinuteTariff byMinuteTariff;

    @Override
    public double getCost(LocalTime duration, CallType callType, Boolean operator) {
        if (CallType.INCOMING.equals(callType) && incomingAnother && incomingInside) {
            return 0;
        }

        int minutes = getTotalMinutes(duration);
        if (firstMin > 0) {
            double totalCost;
            if (minutes <= firstMin) {
                totalCost = (minutes * firstPrice);
                firstMin -= minutes;
            } else {
                totalCost = (firstMin * firstPrice) + ((minutes - firstMin) * byMinuteTariff.getMinutePrice());
                firstMin = 0;
            }
            return totalCost;
        } else {
            return minutes * byMinuteTariff.getMinutePrice();
        }
    }
}
