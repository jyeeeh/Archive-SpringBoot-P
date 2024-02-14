package com.sloop.archive.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class SloopExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = {SloopException.class, Exception.class})
    public String handle(SloopException e){
        log.error("예외 발생 : " + e.getMessage());
        return "error/error";
    }
}
