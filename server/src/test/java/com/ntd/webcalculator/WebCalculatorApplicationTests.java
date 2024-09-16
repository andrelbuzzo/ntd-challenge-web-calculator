package com.ntd.webcalculator;

import com.ntd.webcalculator.calculator.CalculatorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
public class WebCalculatorApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testAddTwoIntegers() throws Exception {
		mockMvc.perform(get("/api/calculator/add?param1=1&param2=2"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(1.0)))
				.andExpect(jsonPath("$.rightOperand", is(2.0)))
				.andExpect(jsonPath("$.result", is(3.0)))
				.andExpect(jsonPath("$.operation", is("+")));
	}

	@Test
	public void testAddTwoFloats() throws Exception {
		mockMvc.perform(get("/api/calculator/add?param1=1.2&param2=2.4"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(1.2)))
				.andExpect(jsonPath("$.rightOperand", is(2.4)))
				.andExpect(jsonPath("$.result", closeTo(3.6, 0.05)))
				.andExpect(jsonPath("$.operation", is("+")));
	}

	@Test
	public void testSubtractTwoIntegersPositiveResult() throws Exception {
		mockMvc.perform(get("/api/calculator/subtract?param1=4&param2=2"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(4.0)))
				.andExpect(jsonPath("$.rightOperand", is(2.0)))
				.andExpect(jsonPath("$.result", is(2.0)))
				.andExpect(jsonPath("$.operation", is("-")));
	}

	@Test
	public void testSubtractTwoIntegersNegativeResult() throws Exception {
		mockMvc.perform(get("/api/calculator/subtract?param1=2&param2=4"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(2.0)))
				.andExpect(jsonPath("$.rightOperand", is(4.0)))
				.andExpect(jsonPath("$.result", is(-2.0)))
				.andExpect(jsonPath("$.operation", is("-")));
	}

	@Test
	public void testSubtractTwoFloatsPositiveResult() throws Exception {
		mockMvc.perform(get("/api/calculator/subtract?param1=3.9&param2=2.8"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(3.9)))
				.andExpect(jsonPath("$.rightOperand", is(2.8)))
				.andExpect(jsonPath("$.result", closeTo(1.1, 0.05)))
				.andExpect(jsonPath("$.operation", is("-")));
	}

	@Test
	public void testSubtractTwoFloatsNegativeResult() throws Exception {
		mockMvc.perform(get("/api/calculator/subtract?param1=2.8&param2=3.9"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(2.8)))
				.andExpect(jsonPath("$.rightOperand", is(3.9)))
				.andExpect(jsonPath("$.result", closeTo(-1.1, 0.05)))
				.andExpect(jsonPath("$.operation", is("-")));
	}

	@Test
	public void testMultiplyTwoIntegers() throws Exception {
		mockMvc.perform(get("/api/calculator/multiply?param1=8&param2=9"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(8.0)))
				.andExpect(jsonPath("$.rightOperand", is(9.0)))
				.andExpect(jsonPath("$.result", is(72.0)))
				.andExpect(jsonPath("$.operation", is("*")));
	}

	@Test
	public void testMultiplyTwoFloats() throws Exception {
		mockMvc.perform(get("/api/calculator/multiply?param1=8.5&param2=9.3"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(8.5)))
				.andExpect(jsonPath("$.rightOperand", is(9.3)))
				.andExpect(jsonPath("$.result", closeTo(79.05, 0.05)))
				.andExpect(jsonPath("$.operation", is("*")));
	}

	@Test
	public void testDivideTwoIntegersEvenly() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param1=6&param2=3"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(6.0)))
				.andExpect(jsonPath("$.rightOperand", is(3.0)))
				.andExpect(jsonPath("$.result", is(2.0)))
				.andExpect(jsonPath("$.operation", is("/")));
	}

	@Test
	public void testDivideTwoIntegersWithFraction() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param1=6&param2=4"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(6.0)))
				.andExpect(jsonPath("$.rightOperand", is(4.0)))
				.andExpect(jsonPath("$.result", closeTo(1.5, 0.05)))
				.andExpect(jsonPath("$.operation", is("/")));
	}

	@Test
	public void testDivideTwoFloats() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param1=6.2&param2=4.8"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(6.2)))
				.andExpect(jsonPath("$.rightOperand", is(4.8)))
				.andExpect(jsonPath("$.result", closeTo(1.292, 0.05)))
				.andExpect(jsonPath("$.operation", is("/")));
	}

	@Test
	public void testDivideZeroNumerator() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param1=0&param2=6"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.leftOperand", is(0.0)))
				.andExpect(jsonPath("$.rightOperand", is(6.0)))
				.andExpect(jsonPath("$.result", is(0.0)))
				.andExpect(jsonPath("$.operation", is("/")));
	}

	@Test
	public void testDivideByZero() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param1=6&param2=0"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAddMissingParam1() throws Exception {
		mockMvc.perform(get("/api/calculator/add?param2=2"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testAddMissingParam2() throws Exception {
		mockMvc.perform(get("/api/calculator/add?param1=1"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testSubtractMissingParam1() throws Exception {
		mockMvc.perform(get("/api/calculator/subtract?param2=2"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testSubtractMissingParam2() throws Exception {
		mockMvc.perform(get("/api/calculator/subtract?param1=1"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testMultiplyMissingParam1() throws Exception {
		mockMvc.perform(get("/api/calculator/multiply?param2=2"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testMultiplyMissingParam2() throws Exception {
		mockMvc.perform(get("/api/calculator/multiply?param1=1"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDivideMissingParam1() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param2=2"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDivideMissingParam2() throws Exception {
		mockMvc.perform(get("/api/calculator/divide?param1=1"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
}
