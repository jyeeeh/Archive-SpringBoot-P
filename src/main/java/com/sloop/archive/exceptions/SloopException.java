package com.sloop.archive.exceptions;

import org.springframework.http.HttpStatus;

public class SloopException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;

    // 생성자
    public SloopException() {
    }

    public SloopException(HttpStatus httpStatus, String message){
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
