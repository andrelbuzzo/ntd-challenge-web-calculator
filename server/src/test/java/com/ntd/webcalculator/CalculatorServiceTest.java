package com.ntd.webcalculator;

import com.ntd.webcalculator.calculator.CalculatorModel;
import com.ntd.webcalculator.calculator.CalculatorService;
import com.ntd.webcalculator.calculator.StringGenerationService;
import com.ntd.webcalculator.exception.DivisionByZeroException;
import com.ntd.webcalculator.exception.InsufficientBalanceException;
import com.ntd.webcalculator.exception.NegativeSquareRootException;
import com.ntd.webcalculator.operation.OperationService;
import com.ntd.webcalculator.record.RecordService;
import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RecordService recordService;

    @Mock
    private StringGenerationService stringGenerationService;

    private CalculatorService calculatorService;
    private User user;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService(
                new OperationService(), userService, recordService, stringGenerationService);
        user = new User("Test User", "test@example.com", "password", null);
        user.setBalance(new BigDecimal("1.00"));
        lenient().when(userService.loadUserByUsernameForUpdate("test@example.com")).thenReturn(user);
    }

    @Test
    void calculatesAndPersistsOperationWithDeductedBalance() {
        CalculatorModel response = calculatorService.calculate(
                "test@example.com", "-1", "-5", "-");

        assertEquals(4d, response.getResult());
        assertEquals(new BigDecimal("0.80"), response.getUserBalance());
        assertEquals(0, new BigDecimal("0.20").compareTo(response.getCost()));
        assertEquals(new BigDecimal("0.80"), user.getBalance());
        verify(recordService).save(eq("-"), eq(user), eq(new BigDecimal("-1.0")),
                eq(new BigDecimal("-5.0")), eq(new BigDecimal("1.00")),
                eq(new BigDecimal("0.80")), eq("4.0"));
    }

    @Test
    void rejectsDivisionByZeroBeforeChargingBalance() {
        assertThrows(DivisionByZeroException.class, () -> calculatorService.calculate(
                "test@example.com", "6", "0", "/"));

        verify(userService, never()).loadUserByUsernameForUpdate(any());
        verify(recordService, never()).save(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void rejectsNegativeSquareRootBeforeChargingBalance() {
        assertThrows(NegativeSquareRootException.class, () -> calculatorService.calculate(
                "test@example.com", "-1", "0", "sqrt"));

        verify(userService, never()).loadUserByUsernameForUpdate(any());
        verify(recordService, never()).save(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void rejectsOperationWhenBalanceDoesNotCoverCost() {
        user.setBalance(new BigDecimal("0.10"));

        assertThrows(InsufficientBalanceException.class, () -> calculatorService.calculate(
                "test@example.com", "2", "3", "-"));

        assertEquals(new BigDecimal("0.10"), user.getBalance());
        verify(recordService, never()).save(any(), any(), any(), any(), any(), any(), any());
    }
}
