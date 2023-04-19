package ru.nexign.spring.boot.billing.model.mapper;

import org.mapstruct.Mapper;
import ru.nexign.spring.boot.billing.model.dto.TariffDto;
import ru.nexign.spring.boot.billing.model.entity.Tariff;

@Mapper(componentModel = "spring")
public interface TariffMapper {

	Tariff tariffDtoToTariff(TariffDto tariffDto);

	TariffDto tariffToTariffDto(Tariff tariff);
}

