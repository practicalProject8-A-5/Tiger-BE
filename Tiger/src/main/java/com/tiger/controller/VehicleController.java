package com.tiger.controller;

import com.tiger.domain.CommonResponseDto;
import com.tiger.domain.UserDetailsImpl;
import com.tiger.domain.member.Member;
import com.tiger.domain.vehicle.Vehicle;
import com.tiger.domain.vehicle.dto.*;
import com.tiger.exception.StatusCode;
import com.tiger.service.OpenDateService;
import com.tiger.service.VehicleService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    private final OpenDateService openDateService;


    @ApiOperation(value = "차량 등록")
    @PostMapping("/management")
    public CommonResponseDto<?> create(@ModelAttribute VehicleRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetails userDetails) {

        Member member = ((UserDetailsImpl) userDetails).getMember();

        Vehicle vehicle = vehicleService.create(requestDto, member.getId());

        openDateService.createEmptyList(vehicle); // 차량 생성시 빈 배열 만들기


        return CommonResponseDto.success(StatusCode.VEHICLE_CREATED, vehicle.getVname());
    }

    @ApiOperation(value = "수입 차량 조회")
    @GetMapping
    public CommonResponseDto<?> readAllByTypeImported(HttpServletRequest request) {
        String type = "수입";

        List<VehicleCommonResponseDto> vehicleCommonResponseDtos = vehicleService.readAllByType(type, request);

        return CommonResponseDto.success(StatusCode.SUCCESS, vehicleCommonResponseDtos);
    }

    @ApiOperation(value = "차량 상세 조회")
    @GetMapping("/{vId}")
    public CommonResponseDto<?> readOne(@PathVariable Long vId,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                        HttpServletRequest request) {

        VehicleDetailResponseDto vehicleDetailResponseDto = vehicleService.readOne(vId, startDate, endDate, request);

        return CommonResponseDto.success(StatusCode.SUCCESS, vehicleDetailResponseDto);
    }


    @ApiOperation(value = "내가 등록한 차량 조회")
    @GetMapping("/management")
    public CommonResponseDto<?> readAllByOwnerId(@AuthenticationPrincipal UserDetails userDetails) {

        Member member = ((UserDetailsImpl) userDetails).getMember();

        List<VehicleOwnerResponseDto> vehicleOwnerResponseDtos = vehicleService.readAllByOwnerId(member.getId());

        return CommonResponseDto.success(StatusCode.SUCCESS, vehicleOwnerResponseDtos);
    }

    @ApiOperation(value = "차량 수정 페이지 요청")
    @GetMapping("/management/{vId}")
    public CommonResponseDto<?> updatePage(@PathVariable Long vId,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        Member member = ((UserDetailsImpl) userDetails).getMember();

        VehicleDetailResponseDto vehicleDetailResponseDto = vehicleService.updatePage(vId, member.getId());

        return CommonResponseDto.success(StatusCode.SUCCESS, vehicleDetailResponseDto);
    }

    @ApiOperation(value = "차량 수정")
    @PutMapping("/management/{vId}")
    public CommonResponseDto<?> update(@PathVariable Long vId,
                                       @ModelAttribute VehicleRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetails userDetails) {

        Member member = ((UserDetailsImpl) userDetails).getMember();

        String name = vehicleService.update(vId, requestDto, member.getId());

        return CommonResponseDto.success(StatusCode.VEHICLE_UPDATED, name);
    }

    @ApiOperation(value = "차량 삭제")
    @DeleteMapping("/management/{vId}")
    public CommonResponseDto<?> delete(@PathVariable Long vId,
                                       @AuthenticationPrincipal UserDetails userDetails) {

        Member member = ((UserDetailsImpl) userDetails).getMember();

        String name = vehicleService.delete(vId, member.getId());

        return CommonResponseDto.success(StatusCode.VEHICLE_DELETED, name);
    }

    @ApiOperation(value = "차량 검색")
    @PostMapping("/search")
    public CommonResponseDto<?> search(@RequestBody VehicleSearch vehicleSearch, HttpServletRequest request) {

        List<VehicleSearchResponseDto> vehicleSearchResponseDtos = vehicleService.search(vehicleSearch, request);

        return CommonResponseDto.success(StatusCode.SUCCESS, vehicleSearchResponseDtos);
    }

}
