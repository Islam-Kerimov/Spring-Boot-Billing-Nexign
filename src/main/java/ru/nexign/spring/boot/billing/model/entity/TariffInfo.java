package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tariff")
public class TariffInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    private String uuid;
    private String name;
    private Integer fixMin;
    private Double fixPrice;
    private Integer firstMin;
    private Double firstPrice;
    private Double minutePrice;
    private boolean incomingInside;
    private boolean outgoingInside;
    private boolean incomingAnother;
    private boolean outgoingAnother;
    private String monetaryUnit;
    private String redirect;
}
