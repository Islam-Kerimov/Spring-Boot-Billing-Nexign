package ru.nexign.spring.boot.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.nexign.spring.boot.billing.controller.ManagerController;
import ru.nexign.spring.boot.billing.model.entity.Subscriber;
import ru.nexign.spring.boot.billing.service.BillingRealTimeService;
import ru.nexign.spring.boot.billing.service.HighPerformanceRatingServerService;

import java.io.File;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpringBootBillingNexignApplication implements CommandLineRunner {

    private final BillingRealTimeService billingRealTimeService;
    private final HighPerformanceRatingServerService highPerformanceRatingServerService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBillingNexignApplication.class, args);
    }

    @Override
    public void run(String... args) {
        boolean directory = (new File("data")).mkdir();
//        String cdrPlusFile = billingRealTimeService.billing();
//        Map<String, Double> totalCost = highPerformanceRatingServerService.computeSubscriberTotalCost(cdrPlusFile);
//        billingRealTimeService.updateBalance(totalCost);
    }
}
