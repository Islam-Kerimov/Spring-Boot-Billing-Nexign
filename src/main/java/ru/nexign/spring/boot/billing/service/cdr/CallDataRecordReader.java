package ru.nexign.spring.boot.billing.service.cdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.domain.CallType;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;
import ru.nexign.spring.boot.billing.model.mapper.CallDataRecordMapper;
import ru.nexign.spring.boot.billing.repository.CallDataRecordRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallDataRecordReader {

	private static final Integer CORRECT_SIZE_INCOMING_DATA = 4;

	private static final Integer MAX_SECONDS_IN_DAY = 86399;

	private static final DateTimeFormatter INPUT_FORMATTER = ofPattern("yyyyMMddHHmmss");

	private static final DateTimeFormatter OUTPUT_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

	private final CallDataRecordRepository callDataRecordRepository;

	private final CallDataRecordMapper callDataRecordMapper;

	/**
	 * Получение тестовых данных биллинга за определенный месяц из БД.
	 *
	 * @return список объектов с тестовыми данными
	 */
	public List<CallDataRecord> read() {
		List<CallDataRecord> callDataRecords = callDataRecordRepository.findAll();
		callDataRecordRepository.deleteAll();
		return callDataRecords;
	}

	/**
	 * Получение данных биллинга за определенный месяц из файла.
	 *
	 * @param cdrFile                     файл с тестовыми данными
	 * @param correctPhoneNumberAndTariff список абонентов
	 * @return список объектов с тестовыми данными обогащенный типами тарифов
	 */
	public List<CallDataRecord> read(String cdrFile, Map<String, String> correctPhoneNumberAndTariff) {
		List<CallDataRecord> dataRecords = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(cdrFile))) {

			while (true) {
				String callData = reader.readLine();
				if (callData == null || callData.isEmpty()) {
					break;
				}

				String[] data = Arrays.stream(callData.split(","))
					.map(String::trim)
					.toArray(String[]::new);

				if (isValid(callData) && correctPhoneNumberAndTariff.containsKey(data[1])) {
//                    log.info("Звонок добавлен в список [{}]", callData);
					dataRecords.add(callDataRecordMapper.cdrToCdrPlus(data, correctPhoneNumberAndTariff.get(data[1])));
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

		if (data.length != CORRECT_SIZE_INCOMING_DATA) {
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
			return startDate.isBefore(endDate) && startDate.until(endDate, SECONDS) <= MAX_SECONDS_IN_DAY;
		} catch (DateTimeParseException dtpe) {
			return false;
		}
	}

	private boolean callType(String callType) {
		return Arrays.stream(CallType.values())
			.anyMatch(value -> value.getIndex().equals(callType));
	}
}
