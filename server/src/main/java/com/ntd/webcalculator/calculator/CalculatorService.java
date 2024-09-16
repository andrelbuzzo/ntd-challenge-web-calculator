package com.ntd.webcalculator.calculator;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

	public Double calculateResult(Double leftOperand, Double rightOperand, String operation) {
		Double result = 0d;

		switch(operation) {
			case "+":
				result = leftOperand + rightOperand;
				break;
			case "-":
				result = leftOperand - rightOperand;
				break;
			case "*":
				result = leftOperand * rightOperand;
				break;
			case "/":
				result = leftOperand / rightOperand;
				break;
			case "^":
				result = Math.pow(leftOperand,rightOperand);
				break;
			default:
				result = 0d;
		}

		return result;
	}

}
