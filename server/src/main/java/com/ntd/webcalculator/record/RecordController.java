package com.ntd.webcalculator.record;

import com.ntd.webcalculator.user.User;
import com.ntd.webcalculator.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
public class RecordController {

    private final RecordService recordService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<RecordPageResponse> getRecords(Authentication authentication,
                                                         @RequestParam(required = false) String search,
                                                         @RequestParam(name = "operationType", required = false) List<String> operationTypes,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "createdAt,desc") String sort) {
        User user = userService.loadUserByUsername(authentication.getName());
        Pageable paging = PageRequest.of(Math.max(page, 0), Math.max(1, Math.min(size, 50)), sort(sort));
        return ResponseEntity.ok(RecordPageResponse.from(
                recordService.filter(search, operationTypes, user, paging)));
    }

    @DeleteMapping()
    public ResponseEntity<String> delete(Authentication authentication, @RequestParam UUID id) {
        User user = userService.loadUserByUsername(authentication.getName());
        boolean isDeleted = recordService.delete(id, user, true);

        return isDeleted ? ResponseEntity.ok("Record with id " + id + " successfully removed") :
                new ResponseEntity<>("Record not found with id: " + id, HttpStatus.NOT_FOUND);

    }

    @PatchMapping("/undelete")
    public ResponseEntity<String> undelete(Authentication authentication, @RequestParam UUID id) {
        User user = userService.loadUserByUsername(authentication.getName());
        boolean isDeleted = recordService.delete(id, user, false);

        return isDeleted ? ResponseEntity.ok("Record with id " + id + " successfully recovered") :
                new ResponseEntity<>("Record not found with id: " + id, HttpStatus.NOT_FOUND);

    }

    private org.springframework.data.domain.Sort sort(String value) {
        String[] parts = value.split(",", 2);
        String property = switch (parts[0]) {
            case "operationType", "cost", "userBalance", "createdAt" -> parts[0];
            default -> "createdAt";
        };
        org.springframework.data.domain.Sort.Direction direction = parts.length > 1
                && "asc".equalsIgnoreCase(parts[1])
                ? org.springframework.data.domain.Sort.Direction.ASC
                : org.springframework.data.domain.Sort.Direction.DESC;
        return org.springframework.data.domain.Sort.by(direction, property);
    }

}
