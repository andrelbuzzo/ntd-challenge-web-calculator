package com.ntd.webcalculator.calculator;

import com.ntd.webcalculator.enums.OperationType;
import com.ntd.webcalculator.exception.DivisionByZeroException;
import com.ntd.webcalculator.exception.InsufficientBalanceException;
import com.ntd.webcalculator.exception.InvalidOperandException;
import com.ntd.webcalculator.exception.NegativeSquareRootException;
import com.ntd.webcalculator.operation.OperationService;
import com.ntd.webcalculator.record.RecordService;
import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import com.ntd.webcalculator.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Log4j2
public class CalculatorService implements Calculator {

    private final OperationService operationService;
    private final UserService userService;
    private final RecordService recordService;
    private final StringGenerationService stringGenerationService;

    @Override
    public Double calculateResult(Double leftOperand, Double rightOperand, String operation) {
        if (leftOperand == null || rightOperand == null
                || !Double.isFinite(leftOperand) || !Double.isFinite(rightOperand)) {
            throw new InvalidOperandException("Operands must be finite numbers");
        }

        return switch (operation) {
            case "+" -> leftOperand + rightOperand;
            case "-" -> leftOperand - rightOperand;
            case "*" -> leftOperand * rightOperand;
            case "/" -> {
                if (rightOperand == 0d) {
                    throw new DivisionByZeroException();
                }
                yield leftOperand / rightOperand;
            }
            case "sqrt", "^" -> {
                if (leftOperand < 0d) {
                    throw new NegativeSquareRootException();
                }
                yield Math.sqrt(leftOperand);
            }
            default -> throw new com.ntd.webcalculator.exception.InvalidOperationException(
                    "Unsupported operation: " + operation);
        };
    }

    @Transactional
    public CalculatorModel calculate(String username, String leftOperand,
                                     String rightOperand, String operation) {
        double left = parseOperand(leftOperand, "leftOperand");
        double right = parseOperand(rightOperand == null ? "0" : rightOperand, "rightOperand");
        OperationType operationType = operationService.checkOperation(operation);

        validateOperands(left, right, operationType);

        User user = userService.loadUserByUsernameForUpdate(username);
        BigDecimal balanceBefore = user.getBalance().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal balance = debit(user, operationType);
        Double result = calculateResult(left, right, operation);

        CalculatorModel response = new CalculatorModel(
                left, right, operation, result, Constants.OPERATION_SUCCESSFUL);
        response.setUserBalance(balance);
        response.setCost(operationType.cost());
        recordService.save(operation, user, BigDecimal.valueOf(left),
                operationType == OperationType.SQUARE_ROOT ? null : BigDecimal.valueOf(right),
                balanceBefore, balance, String.valueOf(result));
        log.info("Operation completed user={}, type={}, balance={}", username, operationType, balance);
        return response;
    }

    @Transactional
    public CalculatorModel generateString(String username) {
        OperationType operationType = OperationType.RANDOM_STRING;
        User user = userService.loadUserByUsernameForUpdate(username);
        BigDecimal balanceBefore = user.getBalance().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal balance = debit(user, operationType);
        String result = stringGenerationService.generateString();

        CalculatorModel response = new CalculatorModel(null, null,
                operationType.name(), null, result);
        response.setUserBalance(balance);
        response.setCost(operationType.cost());
        recordService.save("random_string", user, null, null, balanceBefore, balance, result);
        return response;
    }

    private BigDecimal debit(User user, OperationType operationType) {
        BigDecimal currentBalance = user.getBalance().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal newBalance = currentBalance.subtract(operationType.cost())
                .setScale(2, RoundingMode.HALF_EVEN);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for " + operationType.name().toLowerCase()
                            + ". Required: " + operationType.cost()
                            + ", available: " + currentBalance);
        }
        user.setBalance(newBalance);
        return newBalance;
    }

    private void validateOperands(double left, double right, OperationType operationType) {
        if (operationType == OperationType.DIVISION && right == 0d) {
            throw new DivisionByZeroException();
        }
        if (operationType == OperationType.SQUARE_ROOT && left < 0d) {
            throw new NegativeSquareRootException();
        }
    }

    private double parseOperand(String value, String operandName) {
        try {
            double parsed = Double.parseDouble(value);
            if (!Double.isFinite(parsed)) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new InvalidOperandException(operandName + " must be a valid finite number");
        }
    }
}
