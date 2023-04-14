package ru.nexign.spring.boot.billing.cdr.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Override
    public String toString() {
        return callType + "," + phoneNumber + "," + callStart + "," + callEnd + "\n";
    }
}
