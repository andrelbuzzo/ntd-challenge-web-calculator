package com.ntd.webcalculator.user;

import com.ntd.webcalculator.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);

    Page<User> findAllByRoleAndNameContaining(Role role, String name, Pageable pageable);

    Page<User> findByNameContaining(String name, Pageable pageable);

    boolean existsByUsername(String username);

    Page<User> findAllByRole(Role role, Pageable pageable);

}