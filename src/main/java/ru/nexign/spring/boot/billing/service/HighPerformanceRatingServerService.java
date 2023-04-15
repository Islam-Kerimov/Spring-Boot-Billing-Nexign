package ru.nexign.spring.boot.billing.service;

import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.CallType;
import ru.nexign.spring.boot.billing.model.TariffType;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.tariff.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.io.File.separator;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Comparator.comparing;

@Service
public class HighPerformanceRatingServerService {
    private static final String CDR_FILE_PLUS = "data" + separator + "cdr+.txt";
    private static final DateTimeFormatter INPUT_FORMATTER = ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter OUTPUT_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveInDataBase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CDR_FILE_PLUS))) {

            Map<String, Set<BillingReport>> allSubscribers = readAllFile(reader);
            calculateCost(allSubscribers);
            int i = 0;
        } catch (IOException ioe) {
            throw new RuntimeException("Ошибка во время чтения/записи файла: " + ioe);
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
        billingReport.setCallType(CallType.fromString(info[0]));
//        billingReport.setSubscriberId(info[1]);
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

    public void calculateCost(Map<String, Set<BillingReport>> map) {
        for (Map.Entry<String, Set<BillingReport>> entry : map.entrySet()) {
            Optional<TariffType> tariffType = entry.getValue().stream()
                    .map(BillingReport::getTariffType)
                    .findAny();

            if (tariffType.isPresent()) {
                Tariff tariff = switch (TariffType.valueOf(tariffType.get().name())) {
                    case UNLIMITED -> new UnlimitedTariff();
                    case BY_MINUTE -> new ByMinuteTariff();
                    case ORDINARY -> new OrdinaryTariff();
                    case X -> new XTariff();
                };

                costCall(entry.getValue(), tariff);
            }
        }
    }

    private void costCall(Set<BillingReport> value, Tariff tariff) {
        for (BillingReport subscriber : value) {
            double cost = tariff.addCostCall(subscriber.getCallType(), subscriber.getDuration());
            subscriber.setCost(cost);
        }
    }
}
