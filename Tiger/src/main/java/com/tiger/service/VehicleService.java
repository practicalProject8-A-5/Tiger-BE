package com.tiger.service;

import com.tiger.domain.vehicle.Vehicle;
import com.tiger.domain.vehicle.VehicleImage;
import com.tiger.domain.vehicle.dto.VehicleOwnerResponseDto;
import com.tiger.domain.vehicle.dto.VehicleRequestDto;
import com.tiger.domain.vehicle.dto.VehicleCommonResponseDto;
import com.tiger.exception.CustomException;
import com.tiger.exception.StatusCode;
import com.tiger.repository.VehicleImageRepository;
import com.tiger.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
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
    public String create(VehicleRequestDto requestDto) {

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

        for (String imageUrl : imageUrlList) {
            vehicleImageRepository.save(
                VehicleImage.builder()
                        .imageUrl(imageUrl)
                        .vehicle(vehicle)
                        .build()
            );
        }

        return vehicleRepository.save(vehicle).getName();
    }

    // 종류별 상품 조회
    public List<VehicleCommonResponseDto> readAllByType(String type) {

        List<Vehicle> vehicleList = vehicleRepository.findAllByTypeAndIsValidOrderByModifiedAtDesc(type, true).orElseThrow(()-> {
            throw new CustomException(StatusCode.VEHICLE_NOT_FOUND);
        });

        List<VehicleCommonResponseDto> vehicleCommonResponseDtos = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {

            vehicleCommonResponseDtos.add(
                VehicleCommonResponseDto.builder()
                    .vid(vehicle.getId())
                    .ownerId(vehicle.getOwnerId())
                    .price(vehicle.getPrice())
                    .description(vehicle.getDescription())
                    .location(vehicle.getLocation())
                    .imageList(vehicle.getImages().stream().map(VehicleImage::getImageUrl).collect(Collectors.toList()))
                    .vname(vehicle.getName())
                    .type(vehicle.getType())
                    .years(vehicle.getYears())
                    .fuelType(vehicle.getFuelType())
                    .passengers(vehicle.getPassengers())
                    .transmission(vehicle.getTransmission())
                    .fuelEfficiency(vehicle.getFuelEfficiency())
                    .build());

        }
        return vehicleCommonResponseDtos;
    }

    // 상세 상세 조회
    public VehicleCommonResponseDto readOne(Long vId) {

        return new VehicleCommonResponseDto(vehicleRepository.findByIdAndIsValid(vId, true).orElseThrow(()->{
            throw new CustomException(StatusCode.VEHICLE_NOT_FOUND);
        }));
    }

    // 등록한 상품 조회
    public List<VehicleOwnerResponseDto> readAllByOwnerId(Long ownerId) {

        List<Vehicle> vehicleList = vehicleRepository.findAllByOwnerIdAndIsValidOrderByCreatedAtDesc(ownerId, true).orElseThrow(()-> {
            throw new CustomException(StatusCode.VEHICLE_NOT_FOUND);
        });

        List<VehicleOwnerResponseDto> vehicleOwnerResponseDtos = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {

            vehicleOwnerResponseDtos.add(
                    VehicleOwnerResponseDto.builder()
                            .vid(vehicle.getId())
                            .thumbnail(vehicle.getThumbnail())
                            .vname(vehicle.getName())
                            .price(vehicle.getPrice())
                            .location(vehicle.getLocation())
                            .createdAt(vehicle.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .startDate("2022-09-14") // delete later
                            .endDate("2022-09-14") // delete later
                            .build()
            );
        }
        return vehicleOwnerResponseDtos;
    }

    // 상품 수정
    @Transactional
    public String update(Long vId, VehicleRequestDto requestDto) {

        Vehicle vehicle = vehicleRepository.findByIdAndIsValid(vId, true).orElseThrow(()->{
            throw new CustomException(StatusCode.VEHICLE_NOT_FOUND);
        });

        vehicle.update(requestDto);

        return vehicle.getName();
    }

    // 상품 삭제
    @Transactional
    public String delete(Long vId) {

        Vehicle vehicle = vehicleRepository.findByIdAndIsValid(vId, true).orElseThrow(()->{
            throw new CustomException(StatusCode.VEHICLE_NOT_FOUND);
        });

        vehicle.delete();

        return vehicle.getName();
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
