//package com.tiger.repository;
//
//import com.querydsl.jpa.JPAExpressions;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.tiger.domain.openDate.QOpenDate;
//import com.tiger.domain.order.QOrders;
//import com.tiger.domain.vehicle.QVehicle;
//import com.tiger.domain.vehicle.Vehicle;
//import com.tiger.domain.vehicle.dto.VehicleCommonResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class VehicleCustomRepository {
//
//    private final JPAQueryFactory jpaQueryFactory;
//    QVehicle vehicle = QVehicle.vehicle;
//    QOrders orders = QOrders.orders;
//    QOpenDate openDate = QOpenDate.openDate;
//
//    //상품 검색
//    public List<VehicleCommonResponseDto> searchVehicle(String startDate, String endDate, Double locationX, Double locationY) {
//
//        QVehicle vehicleSub1 = new QVehicle("vehicleSub1");
//        QVehicle vehicleSub2 = new QVehicle("vehicleSub2");
//
//        return jpaQueryFactory.select(new QVehicleCommonResponseDto(vehicle))
//                .from(vehicle)
//                .where(vehicle.id.in(
//                        JPAExpressions.select(vehicle.id)
//                                .from(openDate)
//                                .where(openDate.startDate.loe(LocalDate.parse(startDate))
//                                        .and(openDate.endDate.goe(LocalDate.parse(endDate))
//                                                .and(vehicle.id.ne(
//                                                                JPAExpressions.select(vehicleSub1.id)
//                                                                        .from(orders)
//                                                                        .where(orders.startDate.loe(LocalDate.parse(startDate)).and(orders.endDate.goe(LocalDate.parse(endDate)))))
//                                                        .and(vehicle.id.in(
//                                                                JPAExpressions.select(vehicleSub2.id)
//                                                                        .from(vehicleSub2)
//                                                                        .where(vehicleSub2.locationX.loe(locationX + 0.3)
//                                                                                .and(vehicleSub2.locationX.goe(locationX - 0.3))
//                                                                                .and(vehicleSub2.locationY.loe(locationY + 0.3))
//                                                                                .and(vehicleSub2.locationY.goe(locationY - 0.3))))))))
//                                .fetch()
//                ));
//    }
//
//}
