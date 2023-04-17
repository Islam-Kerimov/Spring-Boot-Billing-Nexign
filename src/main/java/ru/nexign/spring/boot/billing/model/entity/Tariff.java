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
    protected Integer id;
    protected String uuid;
    protected String name;
    protected Integer fixMin;
    protected Double fixPrice;
    protected Integer firstMin;
    protected Double firstPrice;
    protected Double minutePrice;
    protected Boolean incomingInside;
    protected Boolean outgoingInside;
    protected Boolean incomingAnother;
    protected Boolean outgoingAnother;
    protected String monetaryUnit;
    protected String redirect;
    protected String operator;

    @OneToMany(mappedBy = "tariff")
    private List<Subscriber> subscribers = new ArrayList<>();
}
