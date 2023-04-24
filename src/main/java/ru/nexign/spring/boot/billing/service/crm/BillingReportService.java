package ru.nexign.spring.boot.billing.service.crm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.repository.BillingReportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingReportService {

	private final BillingReportRepository billingReportRepository;

	public List<BillingReport> getAllBillingReportBy(String phoneNumber, Sort sort) {
		return billingReportRepository.findAllByPhoneNumber(phoneNumber, sort);
	}
}
