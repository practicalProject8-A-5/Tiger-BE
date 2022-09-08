package com.tiger.utils;

import com.tiger.domain.member.Member;
import com.tiger.domain.opendate.OpenDate;

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

    @Transactional(readOnly = true)
    public void validateClassifiedList(OpenDate requestOpenDate, Long vid) {

        List<OpenDate> findOpenDateList = openDateRepository.findAllByVehicleIdOrderByStartDateAsc(vid).orElseThrow(() -> new CustomException(VEHICLE_NOT_FOUND));

        LocalDate requestStartDate = requestOpenDate.getStartDate();
        LocalDate requestEndDate = requestOpenDate.getEndDate();


        for (OpenDate findOpenDate : findOpenDateList) { // DB에서 가져와서 하나씩 검증
            LocalDate findStartDate = findOpenDate.getStartDate();
            LocalDate findEndDate = findOpenDate.getEndDate();

            if(requestStartDate.isEqual(requestEndDate))

            if (requestEndDate.isBefore(findStartDate)) { // requestEndDate가 DB_startDate보다 과거 시간이면 무조건 인서트
                openDateRepository.save(requestOpenDate);
            }
            if(requestStartDate.isBefore(findStartDate) && requestEndDate.isAfter(findEndDate)){ // 8.12~8.16 => 8.11~8.17
                openDateRepository.save(requestOpenDate);
                openDateRepository.delete(findOpenDate);

            }
            if (requestStartDate.isAfter(findStartDate) && requestEndDate.isBefore(findEndDate)){ //8.12~8.16 => 8.13 ~8.15
                openDateRepository.save(requestOpenDate);
                openDateRepository.delete(findOpenDate);
            }

            if(requestStartDate.isEqual(findStartDate)){ // 8.12~8.16 => 8.12~8.14 or 8.12~8.19
                openDateRepository.save(requestOpenDate);
                openDateRepository.delete(findOpenDate);

            }
            if (requestEndDate.isEqual(findEndDate)){ // 8.12~8.16 => 8.08 ~8.16 or 8.13~8.16
                openDateRepository.save(requestOpenDate);
                openDateRepository.delete(findOpenDate);
            }
//            if (findStartDate.isAfter(requestStartDate) && findStartDate.isBefore(requestEndDate)) {
//                return openDate.getId();
//            }
//
//            if ((requestStartDate.isAfter(findStartDate) || requestStartDate.isEqual(findStartDate))
//                    && (requestStartDate.isBefore(findEndDate) || findStartDate.isEqual(findEndDate))) { // 8.12~ 8.17 ->  8.12~ or 8.13~,,, 전부 업데이트
//                System.out.println("여기 들어 감");
//
//                return openDate.getId();
//            }
//
//
//
//        return null;


        }


    }
}