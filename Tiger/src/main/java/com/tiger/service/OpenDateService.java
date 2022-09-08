package com.tiger.service;

import com.tiger.domain.opendate.OpenDate;
import com.tiger.domain.opendate.dto.OpenDateListRequestDto;
import com.tiger.domain.opendate.dto.OpenDateRequestDto;
import com.tiger.domain.vehicle.Vehicle;
import com.tiger.exception.CustomException;
import com.tiger.repository.OpenDateRepository;
import com.tiger.repository.VehicleRepository;
import com.tiger.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.tiger.exception.StatusCode.*;

@Service
@RequiredArgsConstructor
public class OpenDateService {

    private final OpenDateRepository openDateRepository;
    private final VehicleRepository vehicleRepository;

    private final CheckUtil checkUtil;

    @Transactional
    public void createOpenDate(OpenDateListRequestDto openDateListRequestDto, Long vid) {

        Vehicle findVehicle = vehicleRepository.findById(vid).orElseThrow(() -> new CustomException(VEHICLE_NOT_FOUND));

        List<LocalDate> openDateList = new ArrayList<>(openDateListRequestDto.getOpenDateList());

        Collections.sort(openDateList); // 날짜 정렬(빠른 순으로)
        System.out.println("AFTER : " + openDateList);

        // 연속된 날짜 판별
        // startDate, endDate 뽑아서
        List<OpenDateRequestDto> newOpendateList = new ArrayList<>();
        OpenDateRequestDto newOpendate = new OpenDateRequestDto(openDateList.get(0)); //startDate 세팅

        for (int i = 0; i < openDateList.size(); i++) {

            LocalDate getopenDate = openDateList.get(i);

            if (openDateList.size() > i + 1) {

                LocalDate nextDate = getopenDate.plusDays(1);
                if (nextDate.isEqual(openDateList.get(i + 1))) {
                    newOpendate.setEndDate(nextDate);
                } else {
                    if (newOpendate.getEndDate() == null) {
                        newOpendate.setEndDate(getopenDate);
                    }

                    newOpendateList.add(newOpendate);
                    newOpendate = new OpenDateRequestDto(openDateList.get(i + 1));
                }
            }

        }
        newOpendateList.add(newOpendate);

        List<OpenDate> classifiedOpenDateList = newOpendateList.stream()
                //stream으로
                .map(dto -> OpenDate.builder()
                        .startDate(dto.getStartDate())
                        .endDate(dto.getEndDate())
                        .vehicle(findVehicle)
                        .build())
                .collect(Collectors.toList());

        boolean find = openDateRepository.existsByVehicleId(vid); // 해당 vid에 대한 데이터 유무만 검증

        if (!find){ // 새로 생성할때

            openDateRepository.saveAll(classifiedOpenDateList);

        }
        else {
            for (OpenDate requestOpenDate : classifiedOpenDateList) {
                checkUtil.validateClassifiedList(requestOpenDate, findVehicle.getId());

            }
        }
//        openDateRepository.saveAll(classifiedOpenDateList);
    }
}


