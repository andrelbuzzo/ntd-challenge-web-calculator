package com.ntd.webcalculator.exception;

public class NegativeSquareRootException extends RuntimeException {
    public NegativeSquareRootException() {
        super("It is not possible to calculate the square root of a negative number");
    }
}
