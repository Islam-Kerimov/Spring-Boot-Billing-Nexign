package ru.nexign.spring.boot.billing.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Тариф "Безлимит 300".
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnlimitedTariff implements TariffPlan {

	private int fixMin;

	private double fixPrice;

	private double minutePrice;

	@Override
	public double getCost(LocalTime duration, CallType callType, Boolean operator) {
		int minutes = getTotalMinutes(duration);
		if (fixMin > 0) {
			double totalCost = 0;
			if (minutes <= fixMin) {
				fixMin -= minutes;
			} else {
				totalCost += ((minutes - fixMin) * minutePrice);
				fixMin = 0;
			}
			return totalCost;
		} else {
			return minutes * minutePrice;
		}
	}
}
