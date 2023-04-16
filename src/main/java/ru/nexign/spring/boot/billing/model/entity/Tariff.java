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
    private String uuid;
    private String name;
    private Integer fixMin;
    private Double fixPrice;
    private Integer firstMin;
    private Double firstPrice;
    private Double minutePrice;
    private Boolean incomingInside;
    private Boolean outgoingInside;
    private Boolean incomingAnother;
    private Boolean outgoingAnother;
    private String monetaryUnit;
    private String redirect;

    @OneToMany(mappedBy = "tariff")
    private List<Subscriber> subscribers = new ArrayList<>();
}
