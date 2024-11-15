package com.ntd.webcalculator.calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
