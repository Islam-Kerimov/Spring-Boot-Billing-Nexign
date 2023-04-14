package ru.nexign.spring.boot.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.nexign.spring.boot.billing.cdr.service.CallDataRecordReader;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SpringBootBillingNexignApplication implements CommandLineRunner {

    private final CallDataRecordReader reader;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootBillingNexignApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Выгрузка звонков абонентов в файл crd.txt");
        int records = reader.read();
        log.info("Выгружено {} звонков", records);
    }
}
