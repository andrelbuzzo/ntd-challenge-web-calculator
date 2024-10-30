package com.ntd.webcalculator.calculator;

import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CalculatorService {

	public Double calculateResult(Double leftOperand, Double rightOperand, String operation) {
		Double result = switch (operation) {
            case "+" -> leftOperand + rightOperand;
            case "-" -> leftOperand - rightOperand;
            case "*" -> leftOperand * rightOperand;
            case "/" -> leftOperand / rightOperand;
            case "^" -> Math.sqrt(leftOperand);
            default -> 0d;
        };

        return result;
	}

}
