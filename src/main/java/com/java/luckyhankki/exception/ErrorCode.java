package com.java.luckyhankki.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ErrorCode {
    /**
     * HttpStatus.BAD_REQUEST : 400
     */
    VALIDATION_FAILED(BAD_REQUEST, "VALIDATION_FAILED", "유효성 검사에 실패했습니다."),
    INVALID_PRICE_DISCOUNT(BAD_REQUEST, "INVALID_PRICE_DISCOUNT", "할인된 가격은 원가보다 클 수 없습니다."),
    INVALID_PICKUP_TIME_RANGE(BAD_REQUEST,"INVALID_PICKUP_TIME_RANGE", "픽업 시작 시각은 픽업 종료 시간보다 늦을 수 없습니다."),

    /**
     * HttpStatus.UNAUTHORIZED : 401
     */
    UNAUTHORIZED_USER(UNAUTHORIZED, "UNAUTHORIZED_USER", "아이디 또는 비밀번호를 확인해주세요."),

    /**
     * HttpStatus.FORBIDDEN : 403
     */
    FORBIDDEN_USER(FORBIDDEN, "FORBIDDEN_USER", "접근 권한이 없습니다."),
    STORE_NOT_APPROVED(FORBIDDEN, "STORE_NOT_APPROVED", "아직 승인되지 않은 가게입니다. 관리자 승인 후 상품 등록이 가능합니다."),

    /**
     * HttpStatus.NOT_FOUND : 404
     */
    SELLER_NOT_FOUND(NOT_FOUND, "SELLER_NOT_FOUND", "존재하지 않는 판매자입니다."),
    STORE_NOT_FOUND(NOT_FOUND, "STORE_NOT_FOUND", "존재하지 않는 가게입니다."),
    PRODUCT_NOT_FOUND(NOT_FOUND, "PRODUCT_NOT_FOUND", "존재하지 않는 상품입니다."),
    CATEGORY_NOT_FOUND(NOT_FOUND, "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다."),
    RESERVATION_NOT_FOUND(NOT_FOUND, "RESERVATION_NOT_FOUND", "예약 내역이 존재하지 않습니다."),

    /**
     * HttpStatus.CONFLICT : 409
     */
    EMAIL_ALREADY_EXISTS(CONFLICT, "EMAIL_ALREADY_EXISTS", "이미 존재하는 이메일입니다."),
    BUSINESS_NUMBER_ALREADY_EXISTS(CONFLICT, "BUSINESS_NUMBER_ALREADY_EXISTS", "이미 존재하는 사업자등록번호입니다."),
    STORE_ALREADY_EXISTS(CONFLICT, "STORE_ALREADY_EXISTS", "이미 등록된 가게가 있습니다."),
    CATEGORY_ALREADY_EXISTS(CONFLICT, "CATEGORY_ALREADY_EXISTS", "이미 존재하는 카테고리 명입니다."),
    RESERVATION_CANCEL_FAILED(CONFLICT, "RESERVATION_CANCEL_FAILED", "해당 예약건은 취소가 불가능합니다."),

    /**
     * HttpStatus.INTERNAL_SERVER_ERROR : 500
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "내부 서버 에러입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
