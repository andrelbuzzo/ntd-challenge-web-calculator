package com.ntd.webcalculator;

import com.ntd.webcalculator.record.Record;
import com.ntd.webcalculator.record.RecordRepository;
import com.ntd.webcalculator.security.TokenService;
import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserRepository;
import com.ntd.webcalculator.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecordControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private TokenService tokenService;

    private User userA;
    private User userB;
    private Record recordA;
    private Record recordB;

    @AfterEach
    void cleanUp() {
        if (recordA != null && recordA.getId() != null) {
            recordRepository.deleteById(recordA.getId());
        }
        if (recordB != null && recordB.getId() != null) {
            recordRepository.deleteById(recordB.getId());
        }
        if (userA != null && userA.getId() != null) {
            userRepository.deleteById(userA.getId());
        }
        if (userB != null && userB.getId() != null) {
            userRepository.deleteById(userB.getId());
        }
    }

    @Test
    void recordsAreScopedToTheAuthenticatedUser() throws Exception {
        userA = userService.save(new User("A", uniqueUsername("records-a"), "123456", null));
        userB = userService.save(new User("B", uniqueUsername("records-b"), "123456", null));
        recordA = saveRecord(userA, "ADDITION", "A response");
        recordB = saveRecord(userB, "DIVISION", "B response");

        mockMvc.perform(get("/api/v1/records?search=response")
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].operationType").value("ADDITION"));
    }

    @Test
    void userCannotDeleteAnotherUsersRecord() throws Exception {
        userA = userService.save(new User("A", uniqueUsername("records-delete-a"), "123456", null));
        userB = userService.save(new User("B", uniqueUsername("records-delete-b"), "123456", null));
        recordA = saveRecord(userA, "ADDITION", "A response");

        mockMvc.perform(delete("/api/v1/records")
                        .param("id", recordA.getId().toString())
                        .header("Authorization", bearer(userB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/records")
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].operationType", is("ADDITION")));

        org.junit.jupiter.api.Assertions.assertFalse(recordRepository.findById(recordA.getId()).orElseThrow().isDeleted());
    }

    @Test
    void filtersRepeatedOperationTypesAndReturnsPaginationMetadata() throws Exception {
        userA = userService.save(new User("A", uniqueUsername("records-filter"), "123456", null));
        recordA = saveRecord(userA, "ADDITION", "addition response");
        recordB = saveRecord(userA, "DIVISION", "division response");

        mockMvc.perform(get("/api/v1/records")
                        .param("operationType", "addition, division")
                        .param("operationType", "ADDITION")
                        .param("page", "1")
                        .param("size", "1")
                        .param("sort", "operationType,asc")
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].operationType").value("DIVISION"));
    }

    @Test
    void rejectsUnsupportedOperationTypeFilter() throws Exception {
        userA = userService.save(new User("A", uniqueUsername("records-invalid-filter"), "123456", null));

        mockMvc.perform(get("/api/v1/records")
                        .param("operationType", "NOT_AN_OPERATION")
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unsupported operation type: NOT_AN_OPERATION"));
    }

    @Test
    void ownDeleteSoftDeletesRecordFromNormalListings() throws Exception {
        userA = userService.save(new User("A", uniqueUsername("records-soft-delete"), "123456", null));
        recordA = saveRecord(userA, "ADDITION", "A response");

        mockMvc.perform(delete("/api/v1/records")
                        .param("id", recordA.getId().toString())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/records")
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content", hasSize(0)));

        org.junit.jupiter.api.Assertions.assertTrue(recordRepository.findById(recordA.getId()).orElseThrow().isDeleted());

        mockMvc.perform(patch("/api/v1/records/undelete")
                        .param("id", recordA.getId().toString())
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/records")
                        .header("Authorization", bearer(userA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    private Record saveRecord(User user, String operation, String response) {
        return recordRepository.saveAndFlush(new Record(
                operation,
                new BigDecimal("0.10"),
                user,
                new BigDecimal("2.00"),
                new BigDecimal("9.90"),
                response));
    }

    private String bearer(User user) {
        return "Bearer " + tokenService.generateToken(user);
    }

    private String uniqueUsername(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@example.com";
    }
}
