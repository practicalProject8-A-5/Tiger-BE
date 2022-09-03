package com.tiger.domain;

import com.tiger.exception.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponseDto<T> {

    private boolean result;
    private Status status;
    private T output;

    public static <T> CommonResponseDto<T> success(T output) {
        return new CommonResponseDto<>(true, new Status(StatusCode.SUCCESS.getHttpStatus().toString(), StatusCode.SUCCESS.getMessage()), output);
    }

    public static <T> CommonResponseDto<T> fail(StatusCode statusCode) {
        return new CommonResponseDto<>(false, new Status(statusCode.getHttpStatus().toString(),statusCode.getMessage()), null);
    }

    @Getter
    @AllArgsConstructor
    static class Status {
        private String code;
        private String msg;
    }
}