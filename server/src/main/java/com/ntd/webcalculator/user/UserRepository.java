package com.ntd.webcalculator.user;

import com.ntd.webcalculator.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findAll(Pageable pageable);

    Optional<User> findByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select user from User user where user.username = :username")
    Optional<User> findByUsernameForUpdate(@Param("username") String username);

    Page<User> findAllByRoleAndNameContaining(Role role, String name, Pageable pageable);

    Page<User> findByNameContaining(String name, Pageable pageable);

    boolean existsByUsername(String username);

    Page<User> findAllByRole(Role role, Pageable pageable);

}
