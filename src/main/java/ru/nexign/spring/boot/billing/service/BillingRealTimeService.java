package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.repository.SubscriberRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.io.File.separator;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingRealTimeService {
    private static final String CDR_FILE = "data" + separator + "cdr.txt";
    private static final String CDR_FILE_PLUS = "data" + separator + "cdr+.txt";

    private final CallDataRecordReader callDataRecordReader;
    private final CallDataRecordWriter callDataRecordWriter;
    private final SubscriberRepository subscriberRepository;

    @Transactional
    public String billing() {
        log.info("Выгрузка звонков всех абонентов в файл crd.txt");
        List<CallDataRecord> dataRecords = callDataRecordReader.read();
        int records = callDataRecordWriter.write(dataRecords, CDR_FILE);
        log.info("Выгружено {} звонков в файл crd.txt", records);


        log.info("Авторизация и выгрузка данных абонентов 'Ромашка' " +
                "с балансом больше нуля в файл crd+.txt");
        Set<Subscriber> correctSubscribers = subscriberRepository.findAllByBalanceAndOperator();
        List<CallDataRecord> dataRecordsWithTariff = callDataRecordReader.read(CDR_FILE, correctSubscribers);
        int validRecords = callDataRecordWriter.write(dataRecordsWithTariff, CDR_FILE_PLUS);
        log.info("Выгружено {} звонков в файл crd+.txt", validRecords);
        return CDR_FILE_PLUS;
    }

    public void updateBalance(Map<String, Double> totalCost) {
        Set<Subscriber> subscribersUpdate = subscriberRepository.findAllByPhoneNumberIn(totalCost.keySet());
        subscribersUpdate.forEach(s -> s.setBalance(s.getBalance() - totalCost.get(s.getPhoneNumber())));
        subscriberRepository.saveAll(subscribersUpdate);
        log.info("Баланс {} абонентов изменен в соответствии с длительностью их разговоров", subscribersUpdate.size());
    }
}
