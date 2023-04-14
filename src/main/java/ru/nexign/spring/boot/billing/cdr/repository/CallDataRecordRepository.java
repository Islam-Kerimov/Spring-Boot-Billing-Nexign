package ru.nexign.spring.boot.billing.cdr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.cdr.entity.CallDataRecord;

public interface CallDataRecordRepository extends JpaRepository<CallDataRecord, Integer> {
}
