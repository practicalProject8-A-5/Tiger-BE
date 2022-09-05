package com.tiger.utils;

import com.tiger.domain.member.Member;
import com.tiger.domain.opendate.OpenDate;
import com.tiger.domain.opendate.dto.OpenDateRequestDto;
import com.tiger.exception.CustomException;
import com.tiger.repository.MemberRepository;
import com.tiger.repository.OpenDateRepository;
import com.tiger.repository.OrderRepository;
import com.tiger.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.tiger.exception.StatusCode.*;

@Component
@RequiredArgsConstructor
public class CheckUtil {


    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final OpenDateRepository openDateRepository;


    // 회원 검증
    @Transactional(readOnly = true)
    public Member validateMember(long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }


    //openDate 검증
    @Transactional(readOnly = true)
    public Long validateOpenDate(OpenDateRequestDto openDateRequestDto, Long vid) {
        List<OpenDate> openDateList = openDateRepository.findAllByVehicleIdOrderByStartDateAsc(vid).orElseThrow(() -> new CustomException(VEHICLE_NOT_FOUND));
        LocalDate requestStartDate = openDateRequestDto.getStartDate();
        LocalDate requestEndDate = openDateRequestDto.getEndDate();

        for (OpenDate openDate : openDateList) {
            LocalDate findStartDate = openDate.getStartDate();
            LocalDate findEndDate = openDate.getEndDate();

            if (requestEndDate.isBefore(findStartDate)){ // requestEndDate가 DB_startDate보다 과거 시간 무조건 인서트
                return null;
            }

            if(requestStartDate.isAfter(findEndDate)) { // 인자보다 미래 시간
                System.out.println("건너뛰기~~~");continue;
            }
            if (findStartDate.isAfter(requestStartDate) && findStartDate.isBefore(requestEndDate)){
                return openDate.getId();
            }

            if ((requestStartDate.isAfter(findStartDate) || requestStartDate.isEqual(findStartDate))
                    && (requestStartDate.isBefore(findEndDate) || findStartDate.isEqual(findEndDate))) { // 8.12~ 8.17 ->  8.12~ or 8.13~,,, 전부 업데이트
                System.out.println("여기 들어 감");

                return openDate.getId();
            }


        }
        return null;
    }
}