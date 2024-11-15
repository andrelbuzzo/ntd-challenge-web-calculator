package com.ntd.webcalculator.user;

import com.ntd.webcalculator.dto.AuthenticationRequest;
import com.ntd.webcalculator.dto.AuthenticationResponse;
import com.ntd.webcalculator.dto.UserRequest;
import com.ntd.webcalculator.dto.UserResponse;
import com.ntd.webcalculator.enums.Role;
import com.ntd.webcalculator.security.AuthenticationService;
import com.ntd.webcalculator.util.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;
    private final AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) Role role,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {
        log.info("/getUsers");
        Pageable paging = PageRequest.of(page, size);
        List<User> users = userService.filter(role, name, paging);
        List<UserResponse> resp = users.stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        User user = mapper.toModel(request);
        user = userService.save(user);
        UserResponse resp = mapper.toResponse(user);
        return ResponseEntity.created(URI.create(user.getId().toString())).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
//		log.info("/login");
//		log.info(request);
        String token = authenticationService.authenticate(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

}