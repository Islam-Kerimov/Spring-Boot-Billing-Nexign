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
    private String phoneNumber;
    private String callType;
    private LocalDateTime callStart;
    private LocalDateTime callEnd;
    private LocalTime duration;
    @Transient
    private TariffType tariffType;
    @Column(columnDefinition = "NUMERIC")
    private Double cost;
}
