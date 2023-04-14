package ru.nexign.spring.boot.billing.cdr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.cdr.entity.CallDataRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.io.File.separator;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallDataRecordWriter {

    public int write(List<CallDataRecord> callDataRecords) {
        int countRecord = 0;
        try (FileWriter writer = new FileWriter("data" + separator + "cdr.txt")) {
            for (CallDataRecord info : callDataRecords) {
                writer.write(info.toString());
                countRecord++;
            }
        } catch (IOException ioe) {
            log.error("Ошибка во время записи файла: " + ioe);
        }
        return countRecord;
    }
}
