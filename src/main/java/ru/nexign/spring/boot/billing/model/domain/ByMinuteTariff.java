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
public class ByMinuteTariff implements TariffPlan {
    private double minutePrice;

    @Override
    public double getCost(LocalTime duration, CallType callType, Boolean operator) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }
        return minutes * minutePrice;
    }
}
