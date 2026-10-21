package com.ntd.webcalculator;

import com.ntd.webcalculator.record.Record;
import com.ntd.webcalculator.record.RecordRepository;
import com.ntd.webcalculator.record.RecordService;
import com.ntd.webcalculator.operation.OperationService;
import com.ntd.webcalculator.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @Test
    void filtersRecordsForTheAuthenticatedUserAndMapsResponse() {
        User user = new User("User", "user@example.com", "password", null);
        Record record = new Record("ADDITION", new BigDecimal("0.10"), user,
                new BigDecimal("2.00"), new BigDecimal("9.90"), "5");
        PageRequest page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(recordRepository.findAllByUserAndDeletedFalse(user, page))
                .thenReturn(new PageImpl<>(List.of(record), page, 1));

        RecordService service = new RecordService(new OperationService(), recordRepository);
        var response = service.filter(null, null, user, page);

        assertEquals(1, response.getTotalElements());
        assertEquals("ADDITION", response.getContent().getFirst().operationType());
        assertEquals(new BigDecimal("9.90"), response.getContent().getFirst().userBalance());
        verify(recordRepository).findAllByUserAndDeletedFalse(user, page);
    }

    @Test
    void softDeletesOnlyARecordOwnedByTheAuthenticatedUser() {
        User user = new User("User", "user@example.com", "password", null);
        UUID id = UUID.randomUUID();
        Record record = new Record("ADDITION", new BigDecimal("0.10"), user,
                new BigDecimal("2.00"), new BigDecimal("9.90"), "5");
        when(recordRepository.findByIdAndUser(id, user)).thenReturn(java.util.Optional.of(record));

        RecordService service = new RecordService(new OperationService(), recordRepository);

        assertEquals(true, service.delete(id, user, true));
        assertTrue(record.isDeleted());
        verify(recordRepository).save(record);
    }
}
