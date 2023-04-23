package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.repository.CallDataRecordRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.min;
import static java.lang.Math.random;

@Service
@RequiredArgsConstructor
public class GeneratorCallDataService {
	private static final int PHONE_NUMBER_LENGTH = 9;
	public static int YEAR = 2023;
	public static int MONTH = 0;

	private final SubscriberService subscriberService;
	private final CallDataRecordRepository callDataRecordRepository;

	private String createRandomCallType() {
		String[] callTypes = {"01", "02"};
		return callTypes[new Random().nextInt(callTypes.length)];
	}

	private String createRandomPhoneNumber() {
		String s = "1234567890";
		StringBuilder phoneNumber = new StringBuilder();
		phoneNumber.append("79");

		for (int i = 0; i < PHONE_NUMBER_LENGTH; i++) {
			int ind = new Random().nextInt(s.length());
			phoneNumber.append(s.charAt(ind));
		}
		return phoneNumber.toString();
	}

	private String[] createRandomCallTime() {
		String month = MONTH > 9 ? String.valueOf(MONTH) : "0" + MONTH;
		String[] day = getStartEndDay();
		String[] time = getStartEndTime();

		String callStart = YEAR + month + day[0] + time[0];
		String callEnd = YEAR + month + day[1] + time[1];
		return new String[]{callStart, callEnd};
	}

	private String[] getStartEndDay() {
		int start = (int) ((random() * (31 - 1)) + 1);
		String startDay = start > 9 ? String.valueOf(start) : "0" + start;
		int end = (int) ((random() * ((start + 1) - start)) + start);
		String endDay = end > 9 ? String.valueOf(end) : "0" + end;

		return new String[]{startDay, endDay};
	}

	private String[] getStartEndTime() {
		int startSeconds = LocalTime.MIN.toSecondOfDay();
		int endSeconds = LocalTime.MAX.toSecondOfDay();
		int randomTime = ThreadLocalRandom.current().nextInt(startSeconds, endSeconds);

		LocalTime localTimeStart = LocalTime.ofSecondOfDay(randomTime);
		LocalTime localTimeEnd = LocalTime.ofSecondOfDay(min((int) ((random() * ((randomTime + 1200) - randomTime)) + randomTime), 86399));

		return new String[]{
			localTimeStart.toString().replace(":", ""),
			localTimeEnd.toString().replace(":", "")};
	}

	private CallDataRecord createCDR(String correctPhoneNumber) {
		String randomCallType = createRandomCallType();
		String randomPhoneNumber;
		if (new Random().nextInt(10) < 9) {
			randomPhoneNumber = correctPhoneNumber;
		} else {
			randomPhoneNumber = createRandomPhoneNumber();
		}
		String[] time = createRandomCallTime();

		return CallDataRecord.builder()
			.callType(randomCallType)
			.phoneNumber(randomPhoneNumber)
			.callStart(time[0])
			.callEnd(time[1])
			.build();
	}

	private void iterateMonthReport() {
		MONTH++;
		if (MONTH == 13) {
			MONTH = 1;
			YEAR++;
		}
	}

	public void generate() {
		iterateMonthReport();
		List<Subscriber> subscribersInDb = subscriberService.getAllSubscribers(Sort.by("id"));
		List<CallDataRecord> callDataRecordList = new ArrayList<>();

		for (int i = 0; i < 100; i++) {
			CallDataRecord callDataRecord = createCDR(subscribersInDb.get(new Random().nextInt(subscribersInDb.size())).getPhoneNumber());
			callDataRecordList.add(callDataRecord);
		}

		callDataRecordRepository.saveAll(callDataRecordList);
	}
}
