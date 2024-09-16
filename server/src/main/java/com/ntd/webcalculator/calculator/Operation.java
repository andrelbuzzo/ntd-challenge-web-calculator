package com.ntd.webcalculator.calculator;

import com.ntd.webcalculator.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Operation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Enumerated(EnumType.STRING)
	private OperationType type; // (addition, subtraction, multiplication, division, square_root, random_string)

	private BigDecimal cost;
}
