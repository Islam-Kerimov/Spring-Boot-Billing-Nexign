package ru.nexign.spring.boot.billing.model.domain;


import ru.nexign.spring.boot.billing.model.entity.CallType;

import java.time.LocalTime;

public interface TariffPlan {

    double getCost(LocalTime duration, CallType callType, Boolean operator);
}
