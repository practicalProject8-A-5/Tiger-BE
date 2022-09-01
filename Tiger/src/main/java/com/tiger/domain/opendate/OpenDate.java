package com.tiger.domain.opendate;

import com.tiger.domain.vehicle.Vehicle;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class OpenDate {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "START_DATE",nullable = false)
    private String startDate;

    @Column(name = "END_DATE",nullable = false)
    private String endDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "VID",nullable = false)
    private Vehicle vehicle;


}
