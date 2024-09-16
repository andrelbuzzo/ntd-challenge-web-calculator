package com.ntd.webcalculator.user;

import com.ntd.webcalculator.enums.Role;
import com.ntd.webcalculator.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;

	public User save(User user) {
		if (userRepo.existsByUsername(user.getUsername()))
			throw new ResourceAlreadyExistsException("User", user.getUsername());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}

	public List<User> find() {
		return userRepo.findAll();
	}

	public List<User> findAllByRole(Role role) {
		return userRepo.findAllByRole(role);
	}

	public Optional<User> findByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	public User getUser(Authentication authentication) {
		return findByUsername(authentication.getPrincipal().toString()).get();
	}

}