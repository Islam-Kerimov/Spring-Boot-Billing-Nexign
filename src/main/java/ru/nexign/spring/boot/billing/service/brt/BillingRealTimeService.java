package ru.nexign.spring.boot.billing.service.brt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;
import ru.nexign.spring.boot.billing.service.cdr.CallDataRecordReader;
import ru.nexign.spring.boot.billing.service.cdr.CallDataRecordWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.io.File.separator;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static ru.nexign.spring.boot.billing.service.cdr.GeneratorCallDataService.MONTH;
import static ru.nexign.spring.boot.billing.service.cdr.GeneratorCallDataService.YEAR;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingRealTimeService {
	private static final String DIRECTORY = "report";
	private static final String CDR_FILE = "cdr_%d_%d.txt";
	private static final String CDR_FILE_PLUS = "cdr+_%d_%d.txt";

	private final CallDataRecordReader callDataRecordReader;
	private final CallDataRecordWriter callDataRecordWriter;
	private final SubscriberRepository subscriberRepository;

	@Transactional
	public String billing() {
		log.info("Выгрузка звонков всех абонентов в файл crd.txt");
		List<CallDataRecord> dataRecords = callDataRecordReader.read();
		String cdrFile = DIRECTORY + separator + format(CDR_FILE, YEAR, MONTH);
		int records = callDataRecordWriter.write(dataRecords, cdrFile);
		log.info("Выгружено {} звонков в файл {}", records, cdrFile);


		log.info("Авторизация и выгрузка данных абонентов 'Ромашка' " +
			"с балансом больше нуля в файл crd+.txt");
		Map<String, String> correctPhoneNumberAndTariff = subscriberRepository.findAllByBalanceAndOperator().stream()
			.collect(toMap(Subscriber::getPhoneNumber, e -> e.getTariff().getUuid()));
		List<CallDataRecord> dataRecordsWithTariff = callDataRecordReader.read(cdrFile, correctPhoneNumberAndTariff);
		String cdrPlusFile = DIRECTORY + separator + format(CDR_FILE_PLUS, YEAR, MONTH);
		int validRecords = callDataRecordWriter.write(dataRecordsWithTariff, cdrPlusFile);
		log.info("Выгружено {} звонков в файл {}", validRecords, cdrPlusFile);
		return cdrPlusFile;
	}

	@Transactional
	public void updateBalance(Map<String, Double> totalCost) {
		Set<Subscriber> subscribersUpdate = subscriberRepository.findAllByPhoneNumberIn(totalCost.keySet(), Sort.by("id"));
		subscribersUpdate.forEach(s -> {
			Double fixPrice = s.getTariff().getFixPrice();
			if (fixPrice != null && fixPrice > 0) {
				s.setBalance(s.getBalance() - fixPrice);
			}
			s.setBalance(s.getBalance() - totalCost.get(s.getPhoneNumber()));
		});
		subscriberRepository.saveAll(subscribersUpdate);
		log.info("Баланс {} абонентов изменен в соответствии с длительностью их разговоров", subscribersUpdate.size());
	}
}
