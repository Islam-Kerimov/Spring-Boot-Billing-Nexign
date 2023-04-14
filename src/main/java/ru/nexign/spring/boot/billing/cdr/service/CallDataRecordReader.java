
package ru.nexign.spring.boot.billing.cdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.cdr.entity.CallDataRecord;
import ru.nexign.spring.boot.billing.cdr.repository.CallDataRecordRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CallDataRecordReader {

    private final CallDataRecordRepository repository;
    private final CallDataRecordWriter writer;

    public int read() {
        List<CallDataRecord> callDataRecords = repository.findAll();
        return writer.write(callDataRecords);
    }
}
