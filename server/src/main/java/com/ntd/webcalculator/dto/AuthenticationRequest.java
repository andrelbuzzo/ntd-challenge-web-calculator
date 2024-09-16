package com.ntd.webcalculator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthenticationRequest {

	@NotEmpty(message = "{required.field}")
	@Email(message = "{invalid.field}")
	private String username;

	@NotEmpty(message = "{required.field}")
	private String password;

}