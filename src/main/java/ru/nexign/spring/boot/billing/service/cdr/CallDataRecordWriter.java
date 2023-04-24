package ru.nexign.spring.boot.billing.service.cdr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallDataRecordWriter {

	/**
	 * Записывает данные звонков построчно в файл.
	 *
	 * @param callData список данных звонков для записи
	 * @param cdrFile  файл, в который будут записаны данные
	 * @return количество записанных звонков
	 */
	public int write(List<CallDataRecord> callData, String cdrFile) {
		int countRecord = 0;
		try (FileWriter writer = new FileWriter(cdrFile)) {
			for (CallDataRecord info : callData) {
				writer.write(info.toString());
				countRecord++;
			}
		} catch (IOException ioe) {
			log.error("Ошибка во время записи файла: " + ioe);
		}
		return countRecord;
	}
}
