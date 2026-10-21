package com.ntd.webcalculator.record;

import com.ntd.webcalculator.enums.OperationType;
import com.ntd.webcalculator.operation.OperationService;
import com.ntd.webcalculator.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final OperationService operationService;
    private final RecordRepository recordRepository;

    public Page<RecordResponse> filter(String search, String operationType, User user, Pageable paging) {
        Page<Record> records;
        if (search != null && !search.isBlank()) {
            records = recordRepository.searchByUser(user, search.trim(), paging);
        } else if (operationType != null && !operationType.isBlank()) {
            records = recordRepository.findAllByOperationTypeAndUserAndDeletedFalse(
                    operationType, user, paging);
        } else {
            records = recordRepository.findAllByUserAndDeletedFalse(user, paging);
        }
        return records.map(RecordResponse::from);
    }

    @Transactional
    public void save(String operator, User user, BigDecimal amount,
                     BigDecimal userBalance, String finalResult) {
        OperationType operationType = operationService.checkOperation(operator);
        Record record = new Record(operationType.name(), operationType.cost(), user, amount, userBalance,
                finalResult);
        recordRepository.save(record);
    }

    @Transactional
    public boolean delete(UUID id, User user, boolean delete) {
        return recordRepository.findByIdAndUser(id, user).map(entity -> {
            if (entity.isDeleted() == delete) {
                return true;
            }
            entity.setDeleted(delete);
            recordRepository.save(entity);
            return true;
        }).orElse(false);
    }

}
