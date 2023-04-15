package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.nexign.spring.boot.billing.model.CallType;
import ru.nexign.spring.boot.billing.model.TariffType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "subscriber")
@Entity
@Table(name = "billing_report")
public class BillingReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;
//    @Column(name = "subscriber_id", insertable = false, updatable = false)
//    private Integer subscriberId;
    private CallType callType;
    private LocalDateTime callStart;
    private LocalDateTime callEnd;
    private LocalTime duration;
    private TariffType tariffType;
    private Double cost;
}
