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
public class XTariff implements TariffPlan {
    private boolean incomingInside;
    private boolean outgoingInside;
    private String operator;
    private ByMinuteTariff byMinuteTariff;

    @Override
    public double getCost(LocalTime duration, CallType callType, Boolean operator) {
        if (operator && incomingInside && outgoingInside) {
            return 0;
        }
        int minutes = getTotalMinutes(duration);
        return minutes * byMinuteTariff.getMinutePrice();
    }
}
