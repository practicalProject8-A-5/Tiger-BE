package com.tiger.service;

import com.tiger.domain.opendate.dto.OpenDateRequestDto;
import com.tiger.domain.opendate.OpenDate;
import com.tiger.domain.vehicle.Vehicle;
import com.tiger.exception.CustomException;
import com.tiger.repository.OpenDateRepository;
import com.tiger.repository.VehicleRepository;
import com.tiger.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.tiger.exception.StatusCode.*;

@Service
@RequiredArgsConstructor
public class OpenDateService {

    private final OpenDateRepository openDateRepository;
    private final VehicleRepository vehicleRepository;

    private final CheckUtil checkUtil;

    @Transactional
    public void createOpenDate(OpenDateRequestDto openDateRequestDto, Long vid) {

      Vehicle findVehicle = vehicleRepository.findById(vid).orElseThrow(() -> new CustomException(VEHICLE_NOT_FOUND));

      Long validatedOpenDateId = checkUtil.validateOpenDate(openDateRequestDto,vid);

        System.out.println("validation = " + validatedOpenDateId);

        if (validatedOpenDateId != null) {
            OpenDate openDate = openDateRepository.findById(validatedOpenDateId).orElseThrow(() -> new CustomException(DATE_NOT_FOUND));
            openDate.updateOpenDate(openDateRequestDto);

      }

      else {
            OpenDate openDate   = OpenDate.builder()
                    .startDate(openDateRequestDto.getStartDate())
                    .endDate(openDateRequestDto.getEndDate())
                    .vehicle(findVehicle)
                    .build();
            openDateRepository.save(openDate);
      }



    }



}
