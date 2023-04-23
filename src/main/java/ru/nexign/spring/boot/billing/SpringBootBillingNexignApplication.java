package ru.nexign.spring.boot.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import ru.nexign.spring.boot.billing.service.brt.BillingRealTimeService;
import ru.nexign.spring.boot.billing.service.cdr.GeneratorCallDataService;
import ru.nexign.spring.boot.billing.service.hrs.HighPerformanceRatingServerService;

import java.io.File;
import java.util.Map;

@SpringBootApplication
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class SpringBootBillingNexignApplication implements CommandLineRunner {
	private static final String REPORT_DIRECTORY = "report";

	private final BillingRealTimeService billingRealTimeService;
	private final HighPerformanceRatingServerService highPerformanceRatingServerService;
	private final GeneratorCallDataService generator;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBillingNexignApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// create directory for cdr and cdr+ files
		boolean directory = (new File(REPORT_DIRECTORY)).mkdir();

		// generate cdr records to dataBase
		generator.generate();

		// billing
		String cdrPlusFile = billingRealTimeService.billing();
		Map<String, Double> totalCost = highPerformanceRatingServerService.computeSubscriberTotalCost(cdrPlusFile);
		billingRealTimeService.updateBalance(totalCost);
	}
}
