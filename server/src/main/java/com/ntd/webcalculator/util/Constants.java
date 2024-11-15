package com.ntd.webcalculator.util;

import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static final String OPERATION_SUCCESSFUL = "Operation completed successfully";
    public static final String ERROR_INSUFFICIENT_CREDITS = "Insufficient credits to perform operation";
    public static final String ERROR_DIVISION_BY_ZERO = "ERROR: It's not possible to divide by zero (0)";
    public static final String ERROR_SQRT_NEGATIVE = "ERROR: It's not possible to get the square root of a negative number";

}
