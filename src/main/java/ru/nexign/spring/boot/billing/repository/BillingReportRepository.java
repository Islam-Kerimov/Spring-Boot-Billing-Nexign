package ru.nexign.spring.boot.billing.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;

import java.util.List;

public interface BillingReportRepository extends JpaRepository<BillingReport, Integer> {

	List<BillingReport> findAllByPhoneNumber(String phoneNumber, Sort sort);
}
