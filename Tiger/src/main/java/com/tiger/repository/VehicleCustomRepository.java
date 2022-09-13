package com.tiger.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tiger.domain.openDate.QOpenDate;
import com.tiger.domain.order.QOrders;
import com.tiger.domain.vehicle.QVehicle;
import com.tiger.domain.vehicle.dto.QVehicleCustomResponseDto;
import com.tiger.domain.vehicle.dto.VehicleCustomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VehicleCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    QVehicle vehicle = QVehicle.vehicle;
    QOrders orders = QOrders.orders;
    QOpenDate openDate = QOpenDate.openDate;

    //상품 검색
    public List<VehicleCustomResponseDto> searchVehicle(String startDate, String endDate, Double locationX, Double locationY, String type) {

        return jpaQueryFactory.select(new QVehicleCustomResponseDto(vehicle.id, vehicle.ownerId, vehicle.price, vehicle.description, vehicle.location, vehicle.locationX, vehicle.locationY, vehicle.thumbnail, vehicle.vbrand, vehicle.vname, vehicle.type, vehicle.years, vehicle.fuelType, vehicle.passengers, vehicle.transmission, vehicle.fuelEfficiency))
                .from(vehicle)
                .where(vehicle.type.eq(type).and(vehicle.locationX.between((locationX-0.3), (locationX+0.3))).and(vehicle.locationY.between((locationY-0.3), (locationY+0.3)))
                        .and(vehicle.id.in(JPAExpressions.select(openDate.vehicle.id).from(openDate).where(openDate.startDate.loe(LocalDate.parse(startDate)).and(openDate.endDate.goe(LocalDate.parse(endDate)).and(
                                openDate.vehicle.id.notIn(JPAExpressions.select(orders.vehicle.id).from(orders).where(orders.startDate.goe(LocalDate.parse(startDate)).and(orders.endDate.loe(LocalDate.parse(endDate)))))
                        ))))))
                .fetch();
    }

}
