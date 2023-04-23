package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BillingReportRepository extends JpaRepository<BillingReport, Integer> {
    List<BillingReport> findAllByPhoneNumber(String phoneNumber, Sort sort);
}
