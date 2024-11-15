package com.ntd.webcalculator.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserResponse {

	private Integer id;

	private String name;

	private String username;

	private String Role;

	private BigDecimal balance;

}