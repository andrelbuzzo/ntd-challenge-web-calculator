package com.ntd.webcalculator;

import com.ntd.webcalculator.enums.Status;
import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserRepository;
import com.ntd.webcalculator.user.UserService;
import com.ntd.webcalculator.security.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private User testUser;

    @AfterEach
    void cleanUp() {
        if (testUser != null && testUser.getId() != null) {
            userRepository.deleteById(testUser.getId());
        }
    }

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void realLoginReturnsJwtAndJwtReadsCurrentUser() throws Exception {
        testUser = createUser("security-valid@example.com", "123456");

        String response = mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"security-valid@example.com","password":"123456"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", startsWith("ey")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = response.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("security-valid@example.com"));
    }

    @Test
    void inactiveUserCannotLogin() throws Exception {
        testUser = createUser("security-inactive@example.com", "123456");
        testUser.setStatus(Status.INACTIVE);
        userRepository.save(testUser);

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"security-inactive@example.com","password":"123456"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void previouslyIssuedTokenStopsWorkingAfterUserIsDeactivated() throws Exception {
        testUser = createUser("security-revoked@example.com", "123456");
        String token = tokenService.generateToken(testUser);

        testUser.setStatus(Status.INACTIVE);
        userRepository.save(testUser);

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passwordIsStoredAsBcryptHash() {
        testUser = createUser("security-hash@example.com", "123456");

        org.junit.jupiter.api.Assertions.assertNotEquals("123456", testUser.getPassword());
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches("123456", testUser.getPassword()));
    }

    private User createUser(String username, String password) {
        return userService.save(new User("Security Test", username, password, null));
    }
}
