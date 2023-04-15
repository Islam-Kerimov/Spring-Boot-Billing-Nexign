package ru.nexign.spring.boot.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.nexign.spring.boot.billing.service.BillingRealTimeService;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpringBootBillingNexignApplication implements CommandLineRunner {

    private final BillingRealTimeService billingRealTimeService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBillingNexignApplication.class, args);
    }

    @Override
    public void run(String... args) {
        billingRealTimeService.billing();
    }
}
