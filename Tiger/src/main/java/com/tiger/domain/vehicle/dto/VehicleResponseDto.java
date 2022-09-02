package com.tiger.domain.vehicle.dto;

import com.tiger.domain.vehicle.VehicleImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Getter
@Setter
public class VehicleResponseDto {

    // 상품 주인
    private Long ownerId;

    // 상품 가격
    private Integer price;

    // 상품 설명
    private String description;

    // 상품 위치
    private String location;

    // 상품 이미지
    private List<String> vehicleImages;

    // 차 이름(브랜드 + 모델명)
    private String name;

    // 차 타입(세단, 쿠페, 왜건, SUV, 해치백, 리무진, 밴, 픽업트럭)
    private String type;

    // 차 연식
    private String years;

    // 차 연료 타입(휘발유, 경유, LPG, 전기, 수소)
    private String fuelType;

    // 차 탑승객 수
    private String passengers;

    // 차 변속기 타입(자동, 수동)
    private String transmission;

    // 차 연비
    private String fuelEfficiency;

}
