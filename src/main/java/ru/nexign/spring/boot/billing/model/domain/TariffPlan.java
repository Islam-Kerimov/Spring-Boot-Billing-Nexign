package ru.nexign.spring.boot.billing.model.domain;


import java.time.LocalTime;

public interface TariffPlan {

    default int getTotalMinutes(LocalTime duration) {
        int minutes = duration.getHour() * 60 + duration.getMinute();
        if (duration.getSecond() > 0) {
            minutes += 1;
        }
        return minutes;
    }

    double getCost(LocalTime duration, CallType callType, Boolean operator);
}
