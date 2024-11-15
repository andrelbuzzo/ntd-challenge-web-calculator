package com.ntd.webcalculator.user;

import com.ntd.webcalculator.enums.Role;
import com.ntd.webcalculator.exception.ResourceAlreadyExistsException;
import com.ntd.webcalculator.operation.OperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final OperationService operationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(User user) {
        if (userRepo.existsByUsername(user.getUsername()))
            throw new ResourceAlreadyExistsException("User", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Transactional
    public void update(User user) {
        userRepo.save(user);
    }

    public List<User> filter(Role role, String name, Pageable paging) {
        Page<User> users;
        if (role != null) {
            if (name != null) {
                log.info("filter: role and name");
                users = userRepo.findAllByRoleAndNameContaining(role, name, paging);
            } else {
                log.info("filter: role");
                users = userRepo.findAllByRole(role, paging);
            }
        } else if (name != null) {
            log.info("filter: name");
            users = userRepo.findByNameContaining(name, paging);
        } else {
            log.info("filter: none");
            users = userRepo.findAll(paging);
        }

        return users.getContent();
    }

    public Page<User> findAllByRole(Role role, Pageable paging) {
        return userRepo.findAllByRole(role, paging);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUser(Authentication authentication) {
        return findByUsername(authentication.getPrincipal().toString()).get();
    }

    public BigDecimal checkBalance(User user, String operation) {
        BigDecimal balance = user.getBalance();
        return balance.subtract(operationService.checkOperation(operation).cost()).setScale(2, RoundingMode.HALF_EVEN);
    }

}