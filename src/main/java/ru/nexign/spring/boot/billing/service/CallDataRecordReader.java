package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;
import ru.nexign.spring.boot.billing.model.entity.CallType;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.repository.CallDataRecordRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallDataRecordReader {
    private static final DateTimeFormatter INPUT_FORMATTER = ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter OUTPUT_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CallDataRecordRepository repository;

    public List<CallDataRecord> read() {
        return repository.findAll();
    }

    public List<CallDataRecord> read(String cdrFile, Set<Subscriber> correctSubscribers) {
        List<CallDataRecord> dataRecords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(cdrFile))) {

            Map<String, String> correctPhoneTariff = correctSubscribers.stream()
                    .collect(Collectors.toMap(
                            Subscriber::getPhoneNumber,
                            e -> e.getTariff().getUuid()));

            while (true) {
                String callData = reader.readLine();
                if (callData == null || callData.isEmpty()) {
                    break;
                }

                String[] data = Arrays.stream(callData.split(","))
                        .map(String::trim)
                        .toArray(String[]::new);

                if (isValid(callData) && correctPhoneTariff.containsKey(data[1])) {
//                    log.info("Звонок добавлен в список [{}]", callData);
                    dataRecords.add(CallDataRecord.builder()
                            .callType(data[0])
                            .phoneNumber(data[1])
                            .callStart(data[2])
                            .callEnd(data[3])
                            .tariffType(correctPhoneTariff.get(data[1]))
                            .build());
                }
            }
        } catch (IOException ioe) {
            log.error("Ошибка во время чтения файла: " + ioe);
        }
        return dataRecords;
    }

    private boolean isValid(String callData) {
        String[] data = Arrays.stream(callData.split(","))
                .map(String::trim)
                .toArray(String[]::new);
        if (data.length != 4) {
            log.error("Некорректная информация звонка [{}]", callData);
            return false;
        }
        if (!callType(data[0])) {
            log.error("Указан неправильный тип звонка [{}]", callData);
            return false;
        }
        if (!duration(data[2], data[3])) {
            log.error("Не правильный формат даты звонка [{}]", callData);
            return false;
        }

        return true;
    }

    private boolean duration(String start, String end) {
        try {
            String startInput = LocalDateTime.parse(start, INPUT_FORMATTER).format(OUTPUT_FORMATTER);
            LocalDateTime startDate = LocalDateTime.parse(startInput, OUTPUT_FORMATTER);

            String endInput = LocalDateTime.parse(end, INPUT_FORMATTER).format(OUTPUT_FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(endInput, OUTPUT_FORMATTER);
            return startDate.isBefore(endDate) && startDate.until(endDate, SECONDS) <= 86399;
        } catch (DateTimeParseException dtpe) {
            return false;
        }
    }

    private boolean callType(String callType) {
        return Arrays.stream(CallType.values())
                .anyMatch(value -> value.getIndex().equals(callType));
    }
}
