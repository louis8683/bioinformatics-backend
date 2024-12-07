package com.louislu.bioinformatics.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalRequestArgumentException extends IllegalArgumentException {
    public IllegalRequestArgumentException(String message) {
        super(message);
    }
}