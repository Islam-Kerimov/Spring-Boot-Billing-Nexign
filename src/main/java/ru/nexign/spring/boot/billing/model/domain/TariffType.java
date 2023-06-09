package ru.nexign.spring.boot.billing.model.domain;

/**
 * Типы тарифов.
 */
public enum TariffType {

	UNLIMITED("06"),
	BY_MINUTE("03"),
	ORDINARY("11"),
	X("82");

	private final String tariff;

	TariffType(String tariff) {
		this.tariff = tariff;
	}

	public String getTariff() {
		return tariff;
	}

	/**
	 * Получение типа тарифа по его идентификатору.
	 *
	 * @param tariff идентификатор типа тарифа
	 * @return тип тарифа
	 */
	public static TariffType fromString(String tariff) {
		for (TariffType type : TariffType.values()) {
			if (type.getTariff().equals(tariff)) {
				return type;
			}
		}
		return null;
	}
}
