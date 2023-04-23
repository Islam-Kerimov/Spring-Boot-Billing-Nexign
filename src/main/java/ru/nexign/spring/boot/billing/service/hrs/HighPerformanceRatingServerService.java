package ru.nexign.spring.boot.billing.service.hrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.domain.CallType;
import ru.nexign.spring.boot.billing.model.domain.TariffPlan;
import ru.nexign.spring.boot.billing.model.domain.TariffType;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.model.entity.Tariff;
import ru.nexign.spring.boot.billing.model.mapper.BillingReportMapper;
import ru.nexign.spring.boot.billing.model.mapper.TariffPlanMapper;
import ru.nexign.spring.boot.billing.repository.BillingReportDao;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;
import ru.nexign.spring.boot.billing.repository.TariffRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
import static ru.nexign.spring.boot.billing.model.domain.TariffType.BY_MINUTE;

@Service
@RequiredArgsConstructor
@Slf4j
public class HighPerformanceRatingServerService {
	private final BillingReportDao billingReportDao;
	private final TariffRepository tariffRepository;
	private final SubscriberRepository subscriberRepository;
	private final TariffPlanMapper tariffPlanMapper;
	private final BillingReportMapper billingReportMapper;

	public Map<String, Double> computeSubscriberTotalCost(String cdrPlusFile) {
		Map<String, Double> totalCost;

		try (BufferedReader reader = new BufferedReader(new FileReader(cdrPlusFile))) {

			Map<String, Set<BillingReport>> allReports = readAllFile(reader);
			log.info("Посчитана стоимость звонков всех абонентов");

			Set<BillingReport> reports = allReports.entrySet().stream()
				.flatMap(v -> v.getValue().stream())
				.collect(Collectors.toSet());
			List<String> savedReport = billingReportDao.saveAll(reports);
			log.info("{} данных биллинга сохранено в БД", savedReport.size());

			totalCost = allReports.entrySet().stream()
				.filter(k -> new HashSet<>(savedReport).contains(k.getKey()))
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

	private Map<String, Set<BillingReport>> readAllFile(BufferedReader reader) throws IOException {
		Map<String, Set<BillingReport>> reports = new HashMap<>();
		while (true) {
			String callData = reader.readLine();
			if (callData == null || callData.isEmpty()) {
				break;
			}

			String[] data = Arrays.stream(callData.split(","))
				.map(String::trim)
				.toArray(String[]::new);
			BillingReport billingReport = billingReportMapper.cdrPlusToBillingReport(data);

			Set<BillingReport> infos;
			if (reports.containsKey(data[1])) {
				infos = reports.get(data[1]);
			} else {
				infos = new TreeSet<>(comparing(BillingReport::getStartTime));
			}
			infos.add(billingReport);
			reports.put(data[1], infos);
		}

		calculateCost(reports);

		return reports;
	}

	private void calculateCost(Map<String, Set<BillingReport>> reports) {
		Map<TariffType, Tariff> tariffMap = tariffRepository.findAll().stream()
			.collect(Collectors.toMap(t -> TariffType.fromString(t.getUuid()), t -> t));
		Set<String> byOperatorName = subscriberRepository.findAllByOperatorName("Ромашка").stream()
			.map(Subscriber::getPhoneNumber)
			.collect(Collectors.toSet());

		for (Map.Entry<String, Set<BillingReport>> subscriberReport : reports.entrySet()) {
			Optional<TariffType> tariffType = subscriberReport.getValue().stream()
				.map(BillingReport::getTariffType)
				.findAny();

			if (tariffType.isPresent()) {
				Tariff tariff = tariffMap.get(tariffType.get());

				TariffPlan tariffPlan = null;
				switch (TariffType.fromString(tariff.getUuid())) {
					case BY_MINUTE -> tariffPlan = tariffPlanMapper.tariffToByMinuteTariff(tariff);
					case UNLIMITED -> tariffPlan = tariffPlanMapper.tariffToUnlimitedTariff(tariff);
					case ORDINARY ->
						tariffPlan = tariffPlanMapper.tariffToOrdinaryTariff(tariff, tariffMap.get(BY_MINUTE));
					case X -> tariffPlan = tariffPlanMapper.tariffToXTariff(tariff, tariffMap.get(BY_MINUTE));
				}
				addCostCall(subscriberReport.getValue(), tariffPlan, byOperatorName.contains(subscriberReport.getKey()));
			}
		}
	}

	private void addCostCall(Set<BillingReport> reports, TariffPlan tariff, boolean operatorRomashka) {
		for (BillingReport report : reports) {
			double cost = tariff.getCost(report.getDuration(), CallType.fromString(report.getCallType()), operatorRomashka);
			report.setCost(cost);
		}
	}
}
