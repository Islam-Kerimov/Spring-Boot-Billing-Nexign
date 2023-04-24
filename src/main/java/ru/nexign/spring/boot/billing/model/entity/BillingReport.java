package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.nexign.spring.boot.billing.model.domain.TariffType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "billing_report")
public class BillingReport implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String callType;

	@Column(name = "call_start", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "call_end", nullable = false)
	private LocalDateTime endTime;

	@Column(nullable = false)
	private LocalTime duration;

	@Transient
	private TariffType tariffType;

	@Column(columnDefinition = "NUMERIC", nullable = false)
	private Double cost;
}
