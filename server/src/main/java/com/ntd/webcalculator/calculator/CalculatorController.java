package com.ntd.webcalculator.calculator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @GetMapping("/api/v1/calculator")
    public ResponseEntity<CalculatorModel> calculate(
            Authentication authentication,
            @RequestParam String operator,
            @RequestParam String leftOperand,
            @RequestParam(required = false, defaultValue = "0") String rightOperand) {
        log.info("Calculator request user={}, operator={}", authentication.getName(), operator);
        return ResponseEntity.ok(calculatorService.calculate(
                authentication.getName(), leftOperand, rightOperand, operator));
    }

    @GetMapping("/api/v1/generate-string")
    public ResponseEntity<CalculatorModel> generateString(Authentication authentication) {
        log.info("Random string request user={}", authentication.getName());
        return ResponseEntity.ok(calculatorService.generateString(authentication.getName()));
    }
}
