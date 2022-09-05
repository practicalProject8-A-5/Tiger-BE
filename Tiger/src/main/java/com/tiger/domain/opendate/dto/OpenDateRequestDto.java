package com.tiger.domain.opendate.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class OpenDateRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;


    @Builder
    public OpenDateRequestDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

    }
}
