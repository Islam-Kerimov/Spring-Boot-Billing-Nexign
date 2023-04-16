package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "operator")
public class Operator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;
    private String name;

    @OneToMany(mappedBy = "operator", orphanRemoval = true)
    private List<Subscriber> subscribers = new ArrayList<>(0);
}
