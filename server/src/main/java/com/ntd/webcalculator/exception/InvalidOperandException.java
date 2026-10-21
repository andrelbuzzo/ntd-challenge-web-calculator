package com.ntd.webcalculator.exception;

public class InvalidOperandException extends RuntimeException {
    public InvalidOperandException(String message) {
        super(message);
    }
}
