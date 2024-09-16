package com.ntd.webcalculator.calculator;

import com.ntd.webcalculator.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Record implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@JoinColumn(name = "operation_id")
	@ManyToOne
	private Operation operation;

	@JoinColumn(name = "user_id")
	@ManyToOne
	private User user;

	private BigDecimal amount;

	private BigDecimal userBalance;

	private String operationResponse;

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
}
