package com.ntd.webcalculator.calculator;

import com.ntd.webcalculator.enums.OperationType;
import com.ntd.webcalculator.record.RecordService;
import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import com.ntd.webcalculator.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Log4j2
@RestController
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;
    private final RecordService recordService;
    private final StringGenerationService stringGenerationService;
    private final UserService userService;

    @GetMapping("/api/v1/calculator")
    public ResponseEntity<CalculatorModel> index(
            Authentication auth,
            @RequestParam(value = "operator") String operator,
            @RequestParam(value = "leftOperand") String leftOperand,
            @RequestParam(value = "rightOperand") String rightOperand
    ) {
        log.info("Equation [leftOperand={}, operator={}, rightOperand={}]", leftOperand, operator, rightOperand);

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
            return new ResponseEntity<>(new CalculatorModel(leftNumber, rightNumber, operator, null, Constants.ERROR_DIVISION_BY_ZERO), HttpStatus.BAD_REQUEST);
        }

        if (operator.equals("sqrt") && rightNumber <= 0.0) {
            return new ResponseEntity<>(new CalculatorModel(leftNumber, rightNumber, operator, null, Constants.ERROR_SQRT_NEGATIVE), HttpStatus.BAD_REQUEST);
        }

        User user = userService.loadUserByUsername(auth.getName());
        BigDecimal credits = userService.checkBalance(user, operator);
        log.info(credits);

        CalculatorModel finalResult = new CalculatorModel(Constants.ERROR_INSUFFICIENT_CREDITS);

        try {
            if (credits.compareTo(BigDecimal.ZERO) <= 0) {
                credits = user.getBalance();
                return new ResponseEntity<>(finalResult, HttpStatus.LOCKED);
            }

            user.setBalance(credits);
            userService.update(user);

            Double result = calculatorService.calculateResult(leftNumber, rightNumber, operator);
            log.info("result: {}", result);

            finalResult = new CalculatorModel(leftNumber, rightNumber, operator, result, Constants.OPERATION_SUCCESSFUL);
            log.info(finalResult);

            return ResponseEntity.ok(finalResult);
        } catch (Exception e) {
            return new ResponseEntity<>(new CalculatorModel("ERROR: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        } finally {
            recordService.save(operator, user, credits, finalResult.toString());
        }
    }

    @GetMapping("/api/v1/generate-string")
    public ResponseEntity<String> generateString(Authentication auth) {
        log.info("Generate String ->");

        String str;

        User user = userService.loadUserByUsername(auth.getName());
        BigDecimal credits = userService.checkBalance(user, "stringGen");
        log.info(credits);

        String finalResult = Constants.ERROR_INSUFFICIENT_CREDITS;

        try {
            if (credits.compareTo(BigDecimal.ZERO) <= 0) {
                credits = user.getBalance();
                return new ResponseEntity<>(finalResult, HttpStatus.LOCKED);
            }

            user.setBalance(credits);
            userService.update(user);

            str = stringGenerationService.generateString();
            log.info("result: {}", str);

            finalResult = str;

            return ResponseEntity.ok(str);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            recordService.save(OperationType.RANDOM_STRING.name(), user, credits, finalResult);
        }
    }

}
