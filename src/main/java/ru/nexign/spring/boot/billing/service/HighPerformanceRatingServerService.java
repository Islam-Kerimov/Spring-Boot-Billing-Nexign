package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.model.entity.TariffType;
import ru.nexign.spring.boot.billing.model.tariff.*;
import ru.nexign.spring.boot.billing.repository.BillingReportDao;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighPerformanceRatingServerService {
	private static final DateTimeFormatter INPUT_FORMATTER = ofPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter OUTPUT_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

	private final BillingReportDao billingReportDao;
	private final TariffRepository tariffRepository;

	public Map<String, Double> computeSubscriberTotalCost(String cdrPlusFile) {
		Map<String, Double> totalCost;

		try (BufferedReader reader = new BufferedReader(new FileReader(cdrPlusFile))) {

			Map<String, Set<BillingReport>> allReports = readAllFile(reader);
			calculateCost(allReports);
			log.info("Посчитана стоимость звонков всех абонентов");


			Set<BillingReport> reports = allReports.entrySet().stream()
				.flatMap(v -> v.getValue().stream())
				.collect(Collectors.toSet());
			List<String> savedReport = billingReportDao.saveAll(reports);
			log.info("{} данных биллинга сохранено в БД", savedReport.size());

			Set<String> uniqueReportNumbers = new HashSet<>(savedReport);
			totalCost = allReports.entrySet().stream()
				.filter(k -> uniqueReportNumbers.contains(k.getKey()))
				.collect(toMap(
					Map.Entry::getKey,
					v -> v.getValue().stream()
						.map(BillingReport::getCost)
						.mapToDouble(Double::doubleValue)
						.sum()));

		} catch (IOException ioe) {
			throw new RuntimeException("Ошибка во время чтения/записи файла: " + ioe);
		}

		return totalCost;
	}

	private void calculateCost(Map<String, Set<BillingReport>> map) {
		Map<TariffType, Tariff> tariffMap = tariffRepository.findAll().stream()
			.collect(Collectors.toMap(
				t -> TariffType.fromString(t.getUuid()),
				t -> t));

		for (Map.Entry<String, Set<BillingReport>> entry : map.entrySet()) {
			Optional<TariffType> tariffType = entry.getValue().stream()
				.map(BillingReport::getTariffType)
				.findAny();

			if (tariffType.isPresent()) {
				Tariff tariff = tariffMap.get(tariffType.get());
				TariffPlan tariffPlan = null;
				switch (TariffType.fromString(tariff.getUuid())) {
					case UNLIMITED -> tariffPlan = new UnlimitedTariffPlan();
					case BY_MINUTE -> tariffPlan = new ByMinuteTariffPlan();
					case ORDINARY -> tariffPlan = new OrdinaryTariffPlan();
					case X -> tariffPlan = new XTariffPlan();
				}

				costCall(entry.getValue(), tariffPlan);
			}
		}
	}

	private void costCall(Set<BillingReport> value, TariffPlan tariffPlan) {
		for (BillingReport subscriber : value) {
			double cost = tariffPlan.addCostCall(subscriber.getDuration());
			subscriber.setCost(cost);
		}
	}

	private Map<String, Set<BillingReport>> readAllFile(BufferedReader reader) throws IOException {
		Map<String, Set<BillingReport>> map = new HashMap<>();
		while (true) {
			String callData = reader.readLine();
			if (callData == null || callData.isEmpty()) {
				break;
			}

			String[] data = Arrays.stream(callData.split(","))
				.map(String::trim)
				.toArray(String[]::new);
			BillingReport billingReport = getBillingReport(data);

			Set<BillingReport> infos;
			if (map.containsKey(data[1])) {
				infos = map.get(data[1]);
			} else {
				infos = new TreeSet<>(comparing(BillingReport::getCallStart));
			}
			infos.add(billingReport);
			map.put(data[1], infos);
		}
		return map;
	}

	private BillingReport getBillingReport(String[] info) {
		BillingReport billingReport = new BillingReport();
		billingReport.setCallType(info[0]);
		billingReport.setPhoneNumber(info[1]);
		billingReport.setCallStart(getDateTime(info[2]));
		billingReport.setCallEnd(getDateTime(info[3]));
		billingReport.setDuration(getDurationTime(billingReport.getCallStart(), billingReport.getCallEnd()));
		billingReport.setTariffType(TariffType.fromString(info[4]));
		return billingReport;
	}

	private LocalDateTime getDateTime(String str) {
		String inputFormat = LocalDateTime.parse(str, INPUT_FORMATTER).format(OUTPUT_FORMATTER);
		return LocalDateTime.parse(inputFormat, OUTPUT_FORMATTER);
	}

	private LocalTime getDurationTime(LocalDateTime start, LocalDateTime end) {
		long timeSeconds = start.until(end, SECONDS);
		return LocalTime.ofSecondOfDay(timeSeconds);
	}
}
