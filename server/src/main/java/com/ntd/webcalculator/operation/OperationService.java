package com.ntd.webcalculator.operation;

import com.ntd.webcalculator.enums.OperationType;
import com.ntd.webcalculator.exception.InvalidOperationException;
import org.springframework.stereotype.Service;

@Service
public class OperationService {

    public OperationType checkOperation(String operation) {
        return switch (operation) {
            case "+" -> OperationType.ADDITION;
            case "-" -> OperationType.SUBTRACTION;
            case "*" -> OperationType.MULTIPLICATION;
            case "/" -> OperationType.DIVISION;
            case "sqrt", "^" -> OperationType.SQUARE_ROOT;
            case "stringGen", "random_string" -> OperationType.RANDOM_STRING;
            default -> {
                try {
                    yield OperationType.valueOf(operation.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    throw new InvalidOperationException("Unsupported operation: " + operation);
                }
            }
        };
    }

}
