package ru.nexign.spring.boot.billing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nexign.spring.boot.billing.model.entity.BillingReport;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private Integer id;
    private String phoneNumber;
    private String tariffUuid;
    private String operator;
    private List<BillingReportDto> payload;
    private Double totalCost;
    private String monetaryUnit;
}
