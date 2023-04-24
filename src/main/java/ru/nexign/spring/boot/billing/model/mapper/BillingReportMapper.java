package ru.nexign.spring.boot.billing.model.mapper;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.nexign.spring.boot.billing.model.domain.TariffType;
import ru.nexign.spring.boot.billing.model.dto.BillingReportDto;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.parse;
import static java.time.LocalTime.ofSecondOfDay;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Маппер объекта Entity в DTO.
 */
@Mapper(componentModel = "spring")
public abstract class BillingReportMapper {

	private static final DateTimeFormatter INPUT_FORMATTER = ofPattern("yyyyMMddHHmmss");

	private static final DateTimeFormatter OUTPUT_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

	@Named("report")
	public abstract BillingReportDto billingReportToBillingReportDto(BillingReport billingReport);

	@IterableMapping(qualifiedByName = "report")
	public abstract List<BillingReportDto> billingReportListToBillingReportDtoList(List<BillingReport> billingReports);

	public BillingReport cdrPlusToBillingReport(String[] data) {
		if (data == null) {
			return null;
		}

		LocalDateTime start = getDateTime(data[2]);
		LocalDateTime end = getDateTime(data[3]);
		return BillingReport.builder()
			.callType(data[0])
			.phoneNumber(data[1])
			.startTime(start)
			.endTime(end)
			.duration(getDurationTime(start, end))
			.tariffType(TariffType.fromString(data[4]))
			.build();
	}

	private LocalDateTime getDateTime(String str) {
		String inputFormat = parse(str, INPUT_FORMATTER).format(OUTPUT_FORMATTER);
		return parse(inputFormat, OUTPUT_FORMATTER);
	}

	private LocalTime getDurationTime(LocalDateTime start, LocalDateTime end) {
		long timeSeconds = start.until(end, SECONDS);
		return ofSecondOfDay(timeSeconds);
	}
}

