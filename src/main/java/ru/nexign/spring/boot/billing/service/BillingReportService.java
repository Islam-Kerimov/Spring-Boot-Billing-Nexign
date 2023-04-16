package ru.nexign.spring.boot.billing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;
import ru.nexign.spring.boot.billing.repository.BillingReportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingReportService {

    private final BillingReportRepository billingReportRepository;

    public List<BillingReport> getPayload(String phoneNumber) {
        return billingReportRepository.findAllByPhoneNumber(phoneNumber);
    }
}
