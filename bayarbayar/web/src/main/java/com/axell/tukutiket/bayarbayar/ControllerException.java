package com.axell.tukutiket.bayarbayar;

import com.axell.microservices.common.webmodel.WebResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerException {
    @ExceptionHandler(RuntimeException.class)
    public WebResponse handleRuntimeException(RuntimeException runtimeException) {
        return WebResponse.ERROR(runtimeException.getMessage());
    }
}
