package com.ntd.webcalculator.record;

import com.ntd.webcalculator.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
public class RecordController {

    private final RecordService recordService;

    @GetMapping()
    public ResponseEntity<List<Record>> getRecords(@RequestParam(required = false) String operationType,
                                                           @RequestParam(required = false, name = "user_id") User userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size) {
        log.info("/getRecords");
        Pageable paging = PageRequest.of(page, size);
        List<Record> records = recordService.filter(operationType, userId, paging);
        return ResponseEntity.ok(records);
    }

    @DeleteMapping()
    public ResponseEntity<String> delete(@RequestParam UUID id,
                                         @RequestParam(required = false, defaultValue = "true") boolean delete) {
        log.info("id: {}", id);
        boolean isDeleted = recordService.delete(id, delete);

        return isDeleted ? ResponseEntity.ok("Record with id " + id + " successfully removed") :
                new ResponseEntity<>("Record not found with id: " + id, HttpStatus.NOT_FOUND);

    }

    @PatchMapping("/undelete")
    public ResponseEntity<String> undelete(@RequestParam UUID id) {
        boolean isDeleted = recordService.delete(id, false);

        return isDeleted ? ResponseEntity.ok("Record with id " + id + " successfully recovered") :
                new ResponseEntity<>("Record not found with id: " + id, HttpStatus.NOT_FOUND);

    }

}
