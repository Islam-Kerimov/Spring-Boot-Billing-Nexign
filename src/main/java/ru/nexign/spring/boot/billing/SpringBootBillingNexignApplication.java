package ru.nexign.spring.boot.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.nexign.spring.boot.billing.service.BillingRealTimeService;
import ru.nexign.spring.boot.billing.service.GeneratorCallDataService;
import ru.nexign.spring.boot.billing.service.HighPerformanceRatingServerService;

import java.io.File;
import java.util.Map;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpringBootBillingNexignApplication implements CommandLineRunner {

	private final BillingRealTimeService billingRealTimeService;
	private final HighPerformanceRatingServerService highPerformanceRatingServerService;
	private final GeneratorCallDataService generator;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBillingNexignApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// create directory for cdr and cdr+ files
		boolean directory = (new File("data")).mkdir();

		// generate cdr records to dataBase
		generator.generate();

		// billing
		String cdrPlusFile = billingRealTimeService.billing();
		Map<String, Double> totalCost = highPerformanceRatingServerService.computeSubscriberTotalCost(cdrPlusFile);
		billingRealTimeService.updateBalance(totalCost);
	}
}
