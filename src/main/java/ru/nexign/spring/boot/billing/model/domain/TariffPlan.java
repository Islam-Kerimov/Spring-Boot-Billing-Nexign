package ru.nexign.spring.boot.billing.model.domain;

import java.time.LocalTime;

/**
 * Интерфейс для подсчета суммы звонка согласно его тарифу.
 */
public interface TariffPlan {

	/**
	 * Получение времени звонка округленного до минут.
	 *
	 * @param duration точное время проговоренных минут
	 * @return время округленное до минуты
	 */
	default int getTotalMinutes(LocalTime duration) {
		int minutes = duration.getHour() * 60 + duration.getMinute();
		if (duration.getSecond() > 0) {
			minutes += 1;
		}
		return minutes;
	}

	/**
	 * Подсчет стоимости звонка согласное его тарифу.
	 *
	 * @param duration точное время проговоренных минут
	 * @param callType тип звонка
	 * @param operator оператор абонента
	 * @return сумма звонка
	 */
	double getCost(LocalTime duration, CallType callType, Boolean operator);
}
