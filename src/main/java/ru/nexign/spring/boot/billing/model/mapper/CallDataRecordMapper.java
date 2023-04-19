package ru.nexign.spring.boot.billing.model.mapper;

import org.mapstruct.Mapper;
import ru.nexign.spring.boot.billing.model.entity.CallDataRecord;

@Mapper(componentModel = "spring")
public abstract class CallDataRecordMapper {

	public CallDataRecord cdrToCdrPlus(String[] data, String tariffType) {
		CallDataRecord.CallDataRecordBuilder callDataRecord = CallDataRecord.builder();

		if (data != null) {
			callDataRecord.callType(data[0]);
			callDataRecord.phoneNumber(data[1]);
			callDataRecord.callStart(data[2]);
			callDataRecord.callEnd(data[3]);
			callDataRecord.tariffType(tariffType);
		}

		return callDataRecord.build();
	}

}

