package com.ntd.webcalculator.calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CalculatorModel {
	private Double leftOperand;
	private Double rightOperand;
	private String operation;
	private final Double result;

	public CalculatorModel(String operation) {
		this(0.0d, 0.0d, operation, 0.0d);
	}

	@Override
	public String toString() {
		return "Equation [leftOperand=" + leftOperand +
				", operation=" + operation +
				", rightOperand=" + rightOperand +
				", result=" + result +
				"]";
	}
}
