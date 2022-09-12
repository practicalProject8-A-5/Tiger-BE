package com.tiger.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tiger.domain.Timestamped;
import com.tiger.domain.member.Member;
import com.tiger.domain.order.dto.OrderRequestDto;
import com.tiger.domain.payment.Payment;
import com.tiger.domain.vehicle.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Orders extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private LocalDateTime returnDate;

    @Column(nullable = false)
    private int totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments;

    // 상태 변경하기
    public void setStatus(Status status){
        this.status = status;
    }
    // 반납 상태로 변경하기
    public void setReturn(){
        this.status = Status.RETURN;
        this.returnDate = LocalDateTime.now();
    }
}
