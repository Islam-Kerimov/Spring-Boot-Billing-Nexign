package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriber")
@Builder
public class Subscriber implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    @Column(name = "phone_number")
    private String phoneNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;
    @Column(columnDefinition = "NUMERIC")
    private Double balance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private Operator operator;
}
