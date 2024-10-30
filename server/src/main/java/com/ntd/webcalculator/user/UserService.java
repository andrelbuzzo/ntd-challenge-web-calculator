package com.ntd.webcalculator.user;

import com.ntd.webcalculator.enums.OperationType;
import com.ntd.webcalculator.enums.Role;
import com.ntd.webcalculator.exception.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
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
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
//        log.info("loadUserByUsername");
//        User user = userRepo.findByUsername(username).get();
//        log.info(user);
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUser(Authentication authentication) {
        return findByUsername(authentication.getPrincipal().toString()).get();
    }

    public BigDecimal checkBalance(User user, String operation) {
        BigDecimal balance = user.getBalance();
        return balance.subtract(checkOperationCost(operation)).setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal checkOperationCost(String operation) {
        return switch (operation) {
            case "+" -> OperationType.ADDITION.cost();
            case "-" -> OperationType.SUBTRACTION.cost();
            case "*" -> OperationType.MULTIPLICATION.cost();
            case "/" -> OperationType.DIVISION.cost();
            case "^" -> OperationType.SQUARE_ROOT.cost();
            default -> OperationType.RANDOM_STRING.cost();
        };
    }

}