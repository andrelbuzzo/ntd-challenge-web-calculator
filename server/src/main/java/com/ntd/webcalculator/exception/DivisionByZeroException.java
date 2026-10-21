package com.ntd.webcalculator.exception;

public class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException() {
        super("It is not possible to divide by zero");
    }
}
