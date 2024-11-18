package com.example.demo.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;


public class DataNotFoundException extends Exception{
    public DataNotFoundException(String message) {
        super(message);
    }
}
