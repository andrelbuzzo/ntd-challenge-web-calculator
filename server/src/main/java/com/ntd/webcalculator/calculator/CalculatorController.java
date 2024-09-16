package com.ntd.webcalculator.calculator;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
public class CalculatorController {

	CalculatorService calculatorService;
	StringGenerationService stringGenerationService;

	public CalculatorController(CalculatorService calculatorService, StringGenerationService stringGenerationService) {
		this.calculatorService = calculatorService;
		this.stringGenerationService = stringGenerationService;
	}

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

		try {
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
	public ResponseEntity<String> generateString() {
		log.info("Generate String ->");

		String result = "";

		try {
			result = stringGenerationService.generateString();
			log.info("result: {}", result);

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

}
