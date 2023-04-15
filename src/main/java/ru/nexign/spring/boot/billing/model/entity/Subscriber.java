package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriber")
public class Subscriber implements Serializable {
    @Id
    private Integer id;
    @Column(name = "phone_number")
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private TariffInfo tariffInfo;
    @Column
    private String uuid;
    private Double balance;
    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Operator operator;
}
