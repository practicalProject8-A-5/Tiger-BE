package com.tiger.domain.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSearch {
    private String startDate;
    private String endDate;
    private String location;
    private Double locationX;
    private Double locationY;
    private String type;
}
