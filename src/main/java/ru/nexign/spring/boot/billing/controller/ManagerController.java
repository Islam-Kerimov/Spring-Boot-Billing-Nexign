package ru.nexign.spring.boot.billing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nexign.spring.boot.billing.model.dto.BillingRequest;
import ru.nexign.spring.boot.billing.model.dto.BillingResponse;
import ru.nexign.spring.boot.billing.service.BillingRealTimeService;

@RestController
@RequestMapping(value = "/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final BillingRealTimeService billingRealTimeService;

    @PatchMapping("/billing")
    public BillingResponse getAllCurrencies(@RequestBody BillingRequest request) {
        if (request.getAction().equals("run")) {
            billingRealTimeService.billing();
        }
        return null;
    }
}
