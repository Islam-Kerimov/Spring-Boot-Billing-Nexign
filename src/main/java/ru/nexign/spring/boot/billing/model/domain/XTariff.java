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
public class XTariff implements TariffPlan {
    private boolean incomingInside;
    private boolean outgoingInside;
    private String operator;
    private ByMinuteTariff byMinuteTariff;

    @Override
    public double addCost(LocalTime duration, CallType callType, Boolean operator) {
        if (operator && incomingInside && outgoingInside) {
            return 0;
        }

        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }
        return minutes * byMinuteTariff.getMinutePrice();
    }
}