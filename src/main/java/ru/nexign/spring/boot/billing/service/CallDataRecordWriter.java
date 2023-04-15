package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallDataRecordWriter {

    public int write(List<CallDataRecord> callData, String cdrFile) {
        int countRecord = 0;
        try (FileWriter writer = new FileWriter(cdrFile)) {
            for (CallDataRecord info : callData) {
                writer.write(info.toString());
                countRecord++;
            }
        } catch (IOException ioe) {
            log.error("Ошибка во время записи файла: " + ioe);
        }
        return countRecord;
    }
}
