package com.tiger.service;

import com.tiger.domain.vehicle.Vehicle;
import com.tiger.domain.vehicle.VehicleImage;
import com.tiger.domain.vehicle.dto.VehicleRequestDto;
import com.tiger.domain.vehicle.dto.VehicleResponseDto;
import com.tiger.repository.VehicleImageRepository;
import com.tiger.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AwsS3Service awsS3Service;
    private final VehicleImageRepository vehicleImageRepository;

    // 상품 등록
    @Transactional
    public Vehicle create(VehicleRequestDto requestDto) {

        List<MultipartFile> multipartFiles = requestDto.getMultipartFiles();

        List<String> imageUrlList = awsS3Service.uploadFile(multipartFiles);

        Vehicle vehicle = Vehicle.builder()
                .ownerId(requestDto.getOwnerId())
                .price(requestDto.getPrice())
                .description(requestDto.getDescription())
                .location(requestDto.getLocation())
                .isValid(true)
                .thumbnail(imageUrlList.get(0))
                .name(requestDto.getName())
                .type(requestDto.getType())
                .years(requestDto.getYears())
                .fuelType(requestDto.getFuelType())
                .passengers(requestDto.getPassengers())
                .transmission(requestDto.getTransmission())
                .fuelEfficiency(requestDto.getFuelEfficiency())
                .build();

        // 사진 업로드


        for (String imageUrl : imageUrlList) {
            vehicleImageRepository.save(
                VehicleImage.builder()
                        .imageUrl(imageUrl)
                        .vehicle(vehicle)
                        .build()
            );
        }

        return vehicleRepository.save(vehicle);
    }

    // 종류별 상품 조회
    public List<VehicleResponseDto> readAllByType(String type) {

        List<Vehicle> vehicleList = vehicleRepository.findAllByTypeAndIsValidOrderByModifiedAtDesc(type, true).orElseThrow(()-> new IllegalArgumentException("유효하지 않은 차종입니다."));

        List<VehicleResponseDto> vehicleResponseDtos = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {

            vehicleResponseDtos.add(
                VehicleResponseDto.builder()
                    .ownerId(vehicle.getOwnerId())
                    .price(vehicle.getPrice())
                    .description(vehicle.getDescription())
                    .location(vehicle.getLocation())
                    .vehicleImages(vehicle.getImages().stream().map(VehicleImage::getImageUrl).collect(Collectors.toList()))
                    .name(vehicle.getName())
                    .type(vehicle.getType())
                    .years(vehicle.getYears())
                    .fuelType(vehicle.getFuelType())
                    .passengers(vehicle.getPassengers())
                    .transmission(vehicle.getTransmission())
                    .fuelEfficiency(vehicle.getFuelEfficiency())
                    .build());

        }
        return vehicleResponseDtos;
    }

    // 상세 상세 조회
    public Vehicle readOne(Long vId) {

        return vehicleRepository.findByIdAndIsValid(vId, true).
                orElseThrow(()-> new IllegalArgumentException("유효하지 않은 상품 식별번호입니다."));
    }

    // 등록한 상품 조회
    public List<Vehicle> readAllByOwnerId(Long ownerId) {

        return vehicleRepository.findAllByOwnerIdAndIsValidOrderByCreatedAtDesc(ownerId, true).orElseThrow(()-> new IllegalArgumentException("유효하지 않은 오너 식별번호입니다."));
    }

    // 상품 수정
    @Transactional
    public Vehicle update(Long vId, VehicleRequestDto requestDto) {

        Vehicle vehicle = VehicleService.this.readOne(vId);

        return vehicle.update(requestDto);
    }

    // 상품 삭제
    @Transactional
    public Vehicle delete(Long vId) {

        Vehicle vehicle = VehicleService.this.readOne(vId);

        return vehicle.delete();
    }

    // 상품 수정 페이지 (오너 마이페이지)

    /* 상품 검색
    public List<Vehicle> search(VehicleSearch vehicleSearch) {
        String location = vehicleSearch.getLocation();
        String startDate = vehicleSearch.getStartDate();
        String endDate = vehicleSearch.getEndDate();
        String type = vehicleSearch.getType();

        vehicleRepository.findAllByLocationAndStartDateAntEndDateAndType(location, startDate, endDate, type);

    }
    */

}
