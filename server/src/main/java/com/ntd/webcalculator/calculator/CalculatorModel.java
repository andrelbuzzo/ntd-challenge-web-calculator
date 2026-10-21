package com.ntd.webcalculator.calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorModel {
    private Double leftOperand;
    private Double rightOperand;
    private String operation;
    private Double result;
    private String message;
    private BigDecimal userBalance;
    private BigDecimal cost;

    public CalculatorModel(Double leftOperand, Double rightOperand, String operation,
                          Double result, String message) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operation = operation;
        this.result = result;
        this.message = message;
    }

    public CalculatorModel(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Equation [leftOperand=" + leftOperand +
                ", operation=" + operation +
                ", rightOperand=" + rightOperand +
                ", result=" + result + "]";
    }
}
