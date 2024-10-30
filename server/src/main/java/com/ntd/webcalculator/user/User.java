package com.ntd.webcalculator.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ntd.webcalculator.enums.Role;
import com.ntd.webcalculator.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"password", "role", "authorities", "enabled", "username",
		"accountNonExpired", "credentialsNonExpired", "accountNonLocked"})
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String username; // email

	private String password;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Enumerated(EnumType.STRING)
	private Role role;

	@NumberFormat(style = NumberFormat.Style.CURRENCY, pattern = "#,###.##")
	private BigDecimal balance = new BigDecimal(10.00);

	public User(String username, String password, Role role, Status status) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.status = status;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
