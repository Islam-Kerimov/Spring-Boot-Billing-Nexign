package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;

public interface CallDataRecordRepository extends JpaRepository<CallDataRecord, Integer> {
}
