package com.tiger.utils;

import com.tiger.domain.bank.Bank;
import com.tiger.domain.member.Member;
import com.tiger.domain.openDate.OpenDate;
import com.tiger.domain.order.Orders;
import com.tiger.domain.order.Status;
import com.tiger.domain.order.dto.OrderRequestDto;
import com.tiger.domain.payment.PayMethod;
import com.tiger.domain.payment.Payment;
import com.tiger.domain.vehicle.Vehicle;
import com.tiger.exception.CustomException;
import com.tiger.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.tiger.exception.StatusCode.OPENDATE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CheckUtilTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OpenDateRepository openDateRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BankRepository bankRepository;


    @Test
    @DisplayName("주문 금액 검증")
    void validatePrice(){
        // given

        Vehicle vehicle = vehicleRepository.findByIdAndIsValid(1L, true).get();

        String imp_uid = "0003"; // 가맹점에서 생성/관리하는 고유 주문번호
        String pay_method = PayMethod.CARD.toString(); // 결제 수단
        int paid_amount = 10000; // 결제 금액
        LocalDate start_date = LocalDate.parse("2022-09-04"); // 시작 날짜
        LocalDate end_date = LocalDate.parse("2022-09-13"); // 종료 날짜
        // when

        OrderRequestDto orderRequestDto = new OrderRequestDto(
                imp_uid, pay_method, paid_amount, start_date, end_date);
        // then
        assertThat(vehicle.getPrice()*(orderRequestDto.getEndDate().compareTo(orderRequestDto.getStartDate())+1))
                .isEqualTo(orderRequestDto.getPaidAmount());


    }

    @Test
    @DisplayName("예약 기간 검증")
    void validatePeriod(){

        // given

        HttpServletRequest request = null;
        Long vehicleId = 5l;

        String imp_uid = "0000"; // 가맹점에서 생성/관리하는 고유 주문번호
        String pay_method = PayMethod.CARD.toString(); // 결제 수단
        int paid_amount = 8000; // 결제 금액
        LocalDate start_date = LocalDate.parse("2022-09-17"); // 시작 날짜
        LocalDate end_date = LocalDate.parse("2022-10-01"); // 종료 날짜
        OrderRequestDto orderRequestDto = new OrderRequestDto(
                imp_uid, pay_method, paid_amount, start_date, end_date);
        boolean flag = true;

        List<OpenDate> openDateList = openDateRepository.findAllByIncludeOrderDateMonth(vehicleId,
                orderRequestDto.getStartDate(),
                orderRequestDto.getEndDate()).orElseThrow(
                () -> new CustomException(OPENDATE_NOT_FOUND)
        );
        Set<LocalDate> openDateSet = new HashSet<>();
        for (OpenDate openDate : openDateList) {
            LocalDate startDate = openDate.getStartDate();
            int i=0;
            while (i<=openDate.getStartDate().until(openDate.getEndDate(), ChronoUnit.DAYS)){
                openDateSet.add(startDate.plusDays(i++));
                System.out.println("size = " + openDate.getStartDate().until(openDate.getEndDate(), ChronoUnit.DAYS));
            }
        }
        for (LocalDate date : openDateSet) {
            System.out.println("1단계 = " + date);
        }

        // 오픈 기간에서 이미 사용중인 기간 제외하기
        List<Orders> ordersList = orderRepository.findAllByVehicleIdAndStatusNot(vehicleId, Status.CANCEL).orElse(null);
        for (Orders order : ordersList) {
            int j=0;
            while (j<=order.getStartDate().until(order.getEndDate(), ChronoUnit.DAYS)){
                openDateSet.remove(order.getStartDate().plusDays(j++));
            }
        }
        // 검증
        int k=0;
        while(k <= orderRequestDto.getEndDate().compareTo(orderRequestDto.getStartDate())){
            if(!openDateSet.contains(orderRequestDto.getStartDate().plusDays(k++))){
                flag = false;
                break;
            }
        }
        for(LocalDate date : openDateSet){
            System.out.println("예약 가능 날짜 = " + date);
        }
        //assertThat(openDateSet.size()).isEqualTo(32);
        //assertThat(flag).isFalse();
        assertThat(flag).isTrue();

    }

    @Test
    @DisplayName("환불 기간 검증")
    void validateReturnPeriod(){
        boolean result = true;
        LocalDate now = LocalDate.now();
        LocalDate asdf = LocalDate.parse("2022-11-03");
        LocalDate asdf2 = LocalDate.parse("2022-11-04");
        Orders order = orderRepository.findById(22l).get();
        if(now.compareTo(order.getStartDate()) >= 0 && !order.getStatus().equals("RESERVED")){
            result = false;
        }
        System.out.println("now.compareTo(order.getStartDate()) = " + now.compareTo(order.getStartDate()));
        System.out.println("asdf = " + asdf.compareTo(order.getStartDate()));
        System.out.println("asdf2 = " + asdf2.compareTo(order.getStartDate()));
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("출금 가능한지 검증")
    void validateBank(){
        boolean result = true;
        //given
        List<Payment> payments = paymentRepository.findAllByOrderId(1l).get();
        // when
        Long sum = 0l;
        for (Payment payment : payments) {
            sum += payment.getPaidAmount();
        }
        Bank bank =bankRepository.findById(1L).orElseThrow(
                () -> new IllegalArgumentException("계좌가 존재하지 않습니다.")
        );
        if(bank.getMoney() - sum < 0){
            result = false;
            // throw new CustomException(PRICE_NOT_FOUND);
        }
        // then
        System.out.println(sum);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("본인 상품 주문 못하게 하기")
    void validateSelfOrder(){
        boolean result = true;
        Member member = memberRepository.findById(5L).get();
        Vehicle vehicle = vehicleRepository.findById(1L).get();
        if(member.getId().equals(vehicle.getOwnerId())){
            result = false;
        }
        assertThat(result).isFalse();
    }


}