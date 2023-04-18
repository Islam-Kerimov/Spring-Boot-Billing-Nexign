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
@Builder
@Entity
@Table(name = "operator")
public class Operator implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;
	private String name;

	@OneToMany(mappedBy = "operator", orphanRemoval = true)
	private List<Subscriber> subscribers = new ArrayList<>(0);
}
