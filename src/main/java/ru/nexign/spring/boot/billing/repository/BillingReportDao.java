package ru.nexign.spring.boot.billing.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class BillingReportDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private static final String SQL_INSERT = "" +
		"INSERT INTO billing_report (phone_number, call_type, call_start, call_end, duration, cost) " +
		"VALUES (:phoneNumber, :callType, :startTime, :endTime, :duration, :cost) " +
		"ON CONFLICT ON CONSTRAINT unique_report DO NOTHING";

	/**
	 * Сохранение данных биллинга в Бд по каджому звонку.
	 *
	 * @param reports данные биллинга
	 * @return список номеров абонентов
	 */
	public List<String> saveAll(Set<BillingReport> reports) {
		List<String> phoneNumbers = new ArrayList<>();

		Map<String, Object> paramMap = new HashMap<>();
		for (BillingReport report : reports) {
			paramMap.put("phoneNumber", report.getPhoneNumber());
			paramMap.put("callType", report.getCallType());
			paramMap.put("startTime", report.getStartTime());
			paramMap.put("endTime", report.getEndTime());
			paramMap.put("duration", report.getDuration());
			paramMap.put("cost", report.getCost());
			if (jdbcTemplate.update(SQL_INSERT, paramMap) > 0) {
				phoneNumbers.add(report.getPhoneNumber());
			}
		}
		return phoneNumbers;
	}
}
