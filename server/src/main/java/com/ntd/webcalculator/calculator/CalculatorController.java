package com.ntd.webcalculator.calculator;

import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final StringGenerationService stringGenerationService;
    private final UserService userService;

	/*@GetMapping("/add")
	public ResponseEntity<CalculatorModel> add(@RequestParam(value = "param1", required = true) String param1,
	                                           @RequestParam(value = "param2", required = true) String param2) {
		try {
			log.info(":: Add Operation ::");
			return ResponseEntity.ok(Calculator.add(Double.parseDouble(param1), Double.parseDouble(param2)));
		} catch (Exception e) {
			return new ResponseEntity<>(new CalculatorModel("+"), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/subtract")
	public ResponseEntity<CalculatorModel> subtract(@RequestParam(value = "param1", required = true) String param1,
	                                                @RequestParam(value = "param2", required = true) String param2) {
		try {
			return ResponseEntity.ok(Calculator.subtract(Double.parseDouble(param1), Double.parseDouble(param2)));
		} catch (Exception e) {
			return new ResponseEntity<>(new CalculatorModel("-"), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/multiply")
	public ResponseEntity<CalculatorModel> multiply(@RequestParam(value = "param1", required = true) String param1,
	                                                @RequestParam(value = "param2", required = true) String param2) {
		try {
			return ResponseEntity.ok(Calculator.multiply(Double.parseDouble(param1), Double.parseDouble(param2)));
		} catch (Exception e) {
			return new ResponseEntity<>(new CalculatorModel("*"), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/divide")
	public ResponseEntity<CalculatorModel> divide(@RequestParam(value = "param1", required = true) String param1,
	                                              @RequestParam(value = "param2", required = true) String param2) {
		try {
			Double denominator = Double.parseDouble(param2);
			if (denominator == 0.0) {
				throw new ArithmeticException();
			}
			return ResponseEntity.ok(Calculator.divide(Double.parseDouble(param1), Double.parseDouble(param2)));
		} catch (Exception e) {
			return new ResponseEntity<>(new CalculatorModel("/"), HttpStatus.BAD_REQUEST);
		}
	}*/

//	@GetMapping("/")
//	public String index(Model model) {
//		model.addAttribute("operator", "+");
//		model.addAttribute("view", "views/calculatorForm");
//		return "base-layout";
//	}

    @GetMapping("/api/v1/calculator")
    public ResponseEntity<CalculatorModel> index(
            Authentication auth,
            @RequestParam(value = "operator") String operator,
            @RequestParam(value = "leftOperand") String leftOperand,
            @RequestParam(value = "rightOperand") String rightOperand
    ) {
        log.info("Equation [leftOperand=" + leftOperand +
                ", operator=" + operator +
                ", rightOperand=" + rightOperand + "]");

        double leftNumber;
        double rightNumber;

        try {
            leftNumber = Double.parseDouble(leftOperand);
        } catch (NumberFormatException ex) {
            leftNumber = 0;
        }

        try {
            rightNumber = Double.parseDouble(rightOperand);
        } catch (NumberFormatException ex) {
            rightNumber = 0;
        }

        if (operator.equals("/") && rightNumber == 0.0) {
            throw new ArithmeticException();
        }

        if (operator.equals("sqrt") && leftNumber == 0.0) {
            throw new ArithmeticException();
        }

        try {
            User user = userService.loadUserByUsername(auth.getName());
            BigDecimal credits = userService.checkBalance(user, operator);
            log.info(credits);

            if (credits.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>(new CalculatorModel("Insufficient credits to perform operation"), HttpStatus.LOCKED);
            }

            Double result = calculatorService.calculateResult(leftNumber, rightNumber, operator);
            log.info("result: {}", result);

            CalculatorModel finalResult = new CalculatorModel(leftNumber, rightNumber, operator, result);
            log.info(finalResult);

            return ResponseEntity.ok(finalResult);
        } catch (Exception e) {
            return new ResponseEntity<>(new CalculatorModel(operator), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/api/v1/generateString")
    public ResponseEntity<String> generateString(Authentication auth) {
        log.info("Generate String ->");

        String str;

        try {
            User user = userService.loadUserByUsername(auth.getName());
            BigDecimal credits = userService.checkBalance(user, "stringGen");
            log.info(credits);

            if (credits.compareTo(BigDecimal.ZERO) <= 0) {
                return new ResponseEntity<>("Insufficient credits to perform operation", HttpStatus.LOCKED);
            }

            str = stringGenerationService.generateString();
            log.info("result: {}", str);

            return ResponseEntity.ok(str);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
