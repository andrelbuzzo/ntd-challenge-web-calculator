package com.ntd.webcalculator.operation;

import com.ntd.webcalculator.enums.OperationType;
import org.springframework.stereotype.Service;

@Service
public class OperationService {

    public OperationType checkOperation(String operation) {
        return switch (operation) {
            case "+" -> OperationType.ADDITION;
            case "-" -> OperationType.SUBTRACTION;
            case "*" -> OperationType.MULTIPLICATION;
            case "/" -> OperationType.DIVISION;
            case "^" -> OperationType.SQUARE_ROOT;
            default -> OperationType.RANDOM_STRING;
        };
    }

}
