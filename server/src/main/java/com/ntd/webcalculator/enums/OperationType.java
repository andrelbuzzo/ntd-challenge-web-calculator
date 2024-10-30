package com.ntd.webcalculator.enums;

import java.math.BigDecimal;

public enum OperationType {
    ADDITION(new BigDecimal(0.1)),
    SUBTRACTION(new BigDecimal(0.2)),
    MULTIPLICATION(new BigDecimal(0.3)),
    DIVISION(new BigDecimal(0.5)),
    SQUARE_ROOT(new BigDecimal(1)),
    RANDOM_STRING(new BigDecimal(2));

    BigDecimal cost;

    OperationType(BigDecimal cost) {
        this.cost = cost;
    }

    public String toString() {
        return this.cost.toString();
    }

    public BigDecimal cost() {
        return this.cost;
    }

}
