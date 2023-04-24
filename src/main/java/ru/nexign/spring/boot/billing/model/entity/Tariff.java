package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tariff")
@Builder
public class Tariff implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column(unique = true, nullable = false)
	private String uuid;

	@Column(unique = true, nullable = false)
	private String name;

	@Column
	private Integer fixMin;

	@Column(columnDefinition = "NUMERIC")
	private Double fixPrice;

	@Column
	private Integer firstMin;

	@Column(columnDefinition = "NUMERIC")
	private Double firstPrice;

	@Column(columnDefinition = "NUMERIC")
	private Double minutePrice;

	@Column
	private Boolean incomingInside;

	@Column
	private Boolean outgoingInside;

	@Column
	private Boolean incomingAnother;

	@Column
	private Boolean outgoingAnother;

	@Column
	private String monetaryUnit;

	@Column
	private String redirect;

	@Column(nullable = false)
	private String operator;

	@OneToMany(mappedBy = "tariff")
	private List<Subscriber> subscribers = new ArrayList<>();
}
