package ru.nexign.spring.boot.billing.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cdr_info")
public class CallDataRecord implements Serializable {

    @Id
    private Integer id;
    @Column
    private String callType;
    @Column
    private String phoneNumber;
    @Column
    private String callStart;
    @Column
    private String callEnd;
    @Transient
    private String tariffType;

    @Override
    public String toString() {
        if (tariffType == null) {
            return callType + "," + phoneNumber + "," + callStart + "," + callEnd + "\n";
        } else {
            return callType + "," + phoneNumber + "," + callStart + "," + callEnd + "," + tariffType + "\n";
        }
    }
}
