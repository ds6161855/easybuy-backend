package com.easybuy.exception;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handle(Exception ex) {

        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("message", ex.getMessage());

        return res;
    }
}