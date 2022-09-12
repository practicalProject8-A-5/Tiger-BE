package com.tiger.service;

import com.tiger.domain.CommonResponseDto;
import com.tiger.domain.bank.Bank;
import com.tiger.domain.member.Member;
import com.tiger.domain.openDate.OpenDate;
import com.tiger.domain.order.Orders;
import com.tiger.domain.order.Status;
import com.tiger.domain.order.dto.OrderRequestDto;
import com.tiger.domain.order.dto.OrderResponseDto;
import com.tiger.domain.payment.PayMethod;
import com.tiger.domain.payment.Payment;
import com.tiger.domain.vehicle.Vehicle;
import com.tiger.exception.CustomException;
import com.tiger.exception.StatusCode;
import com.tiger.repository.*;
import com.tiger.utils.CheckUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tiger.exception.StatusCode.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CheckUtil checkUtil;
    private final OrderRepository orderRepository;
    private final OrderCustomRepository orderCustomRepository;
    private final PaymentRepository paymentRepository;
    private final BankRepository bankRepository;
    private final OpenDateRepository openDateRepository;

    // 주문하기
    @Transactional
    public CommonResponseDto<?> order(HttpServletRequest request, Long vehicleId, OrderRequestDto orderRequestDto){

        // 회원 검증
        Member member = checkUtil.validateMember(1L);
        // 상품 검증
        Vehicle vehicle = checkUtil.validateVehicle(vehicleId);
        // 예약 기간 검증
        checkUtil.validateOrderDate(vehicleId, orderRequestDto);
        // 주문 금액 검증
        checkUtil.validatePrice(orderRequestDto, vehicle);
        // Dto -> Domain
        Orders order = Orders.builder()
                .member(member)
                .startDate(orderRequestDto.getStartDate())
                .endDate(orderRequestDto.getEndDate())
                .status(Status.RESERVED)
                .vehicle(vehicle)
                .build();


        Long order_id = orderRepository.save(order).getId();

        if(order_id == null){
            return CommonResponseDto.fail(ORDER_NOT_FOUND);
        } else {
                PayMethod payMethod = null;
            try { // 결제수단 검증
                payMethod = PayMethod.valueOf(orderRequestDto.getPayMethod().toUpperCase());
            } catch (IllegalArgumentException e) {
                return CommonResponseDto.fail(PAYMETHOD_NOT_FOUND);
            }

            paymentRepository.save(Payment.builder()
                    .order(order)
                    .paidAmount(orderRequestDto.getPaidAmount())
                    .payMethod(payMethod)
                    .build());
            Bank bank = checkUtil.validateBank(order);
            bank.deposit((long) orderRequestDto.getPaidAmount());
            return CommonResponseDto.success(ORDER_SUCCESS, null);
        }
    }

    // 주문 리스트(렌터)
    @Transactional(readOnly = true)
    public CommonResponseDto<?> getOrderListRenter(HttpServletRequest request, String status, int limit, int offset) {

        // 회원 검증
        Member member = checkUtil.validateMember(1L);

        // status 검증
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            return CommonResponseDto.fail(STATUS_NOT_FOUND);
        }
        List<Orders> ordersList =  orderRepository.getOrderList(member.getId(), status, limit, offset).orElse(null);

        return CommonResponseDto.success(ORDERLIST_SUCCESS ,ordersList.stream().map(
                order -> new OrderResponseDto(order)).collect(Collectors.toList()));
    }

    // 환불(렌터)
    @Transactional
    public CommonResponseDto<?> refund(HttpServletRequest request, Long orderId) {

        Orders order = checkUtil.validateOrder(orderId);
        Member member = checkUtil.validateMember(1l);
        // 주문 번호 존재 검증
        // 로그인 유저가 주문한 것인지 확인하는 검증
        checkUtil.validateOwner(member, order);
        // 환불 가능한 상태 검증
        List<Payment> payments = paymentRepository.findAllByOrderId(order.getId()).orElseThrow(
                () -> new CustomException(PAYMENT_NOT_FOUND)
        );
        Long price = 0l;
        for (Payment payment : payments) {
            price += payment.getPaidAmount();
        }
        Bank bank =bankRepository.findById(1L).orElseThrow(
                () -> new CustomException(BANK_NOT_FOUND)
        );
        if(bank.getMoney() - price < 0){
            throw new CustomException(EXCESS_AMOUNT_BANK);
        }

        LocalDate now = LocalDate.now();
        if(now.compareTo(order.getStartDate()) <= 0 || !order.getStatus().equals("RESERVED")){
            throw new CustomException(REFUND_ELIGIBILITY_NOT_FOUND);
        }
        // Bank에서 돈 빠져나감
        bank.withdraw(price);
        // Orders 환불 상태로 변경
        order.setStatus(Status.CANCLE);
        // 상품 사용기간 재오픈(필요한가?)
        return CommonResponseDto.success(CANCLE_SUCCESS, null);
    }

    // 반납 확인
    public CommonResponseDto<?> returnVehicle(HttpServletRequest request, Long orderId){


        // 주문 번호 존재 검증
        Orders order = checkUtil.validateOrder(orderId);
        // 로그인 유저와 오너가 동일한지 검증
        Member member = checkUtil.validateMember(1l);
        checkUtil.validateOwner(member, order);
        // 조기 반납인 경우 남은 기간 재오픈

        // 반납완료로 상태 변경하기
        order.setReturn();
        return CommonResponseDto.success(RETURN_SUCCESS, null);
    }

    //판매리스트 (오너)
    @Transactional(readOnly = true)
    public CommonResponseDto<?> getOrderListOwner(HttpServletRequest request, String status, int limit, int offset) {

        /*
        * 렌터와 코드는 똑같으나, 추후 렌터의 주문리스트와 오너의 주문리스트에 차이를 둘 수도 있을 것 같아 분리함
        * 마지막 리팩토링할 떄 똑같으면, 하나로 합치자.
        * */

        // 멤버검증
        Member member = checkUtil.validateMember(2l);
        // status 검증
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            return CommonResponseDto.fail(STATUS_NOT_FOUND);
        }

        List<OrderResponseDto> ordersList =  orderCustomRepository.getOrderListOwner(member.getId(), status, limit, offset);

        return CommonResponseDto.success(ORDERLIST_SUCCESS, ordersList);
    }

    // 일일 매출 (오너)
    @Transactional(readOnly = true)
    public CommonResponseDto<?> getIncomeListDay(HttpServletRequest request) {

        // 멤버검증
        Member owner = checkUtil.validateMember(2l);
        return CommonResponseDto.success(INCOMELIST_SUCCESS,
                orderCustomRepository.getIncomeListDay(2l, LocalDate.now()));

    }
    // 월 매출 (오너)
    @Transactional(readOnly = true)
    public CommonResponseDto<?> getIncomeListMonth(HttpServletRequest request) {

        // 멤버검증
        Member owner = checkUtil.validateMember(2l);
        return CommonResponseDto.success(INCOMELIST_SUCCESS,
                orderCustomRepository.getIncomeListMonth(2l, LocalDate.now()));
    }

}
