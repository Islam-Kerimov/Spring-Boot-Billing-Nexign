package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cdr_info")
public class CallDataRecord implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	private String callType;

	@Column
	private String phoneNumber;

	@Column(name = "call_start")
	private String startTime;

	@Column(name = "call_end")
	private String endTime;

	@Transient
	private String tariffType;

	@Override
	public String toString() {
		if (tariffType == null) {
			return callType + "," + phoneNumber + "," + startTime + "," + endTime + "\n";
		} else {
			return callType + "," + phoneNumber + "," + startTime + "," + endTime + "," + tariffType + "\n";
		}
	}
}
