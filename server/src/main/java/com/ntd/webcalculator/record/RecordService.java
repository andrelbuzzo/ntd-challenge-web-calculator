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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final OperationService operationService;
    private final RecordRepository recordRepository;

    public Page<RecordResponse> filter(String search, String operationType, User user, Pageable paging) {
        Page<Record> records;
        List<String> types = operationType == null ? List.of() : Arrays.stream(operationType.split(","))
                .map(String::trim).filter(type -> !type.isBlank()).toList();
        if (search != null && !search.isBlank() && !types.isEmpty()) {
            records = recordRepository.searchByUserAndOperationTypes(user, search.trim(), types, paging);
        } else if (search != null && !search.isBlank()) {
            records = recordRepository.searchByUser(user, search.trim(), paging);
        } else if (!types.isEmpty()) {
            records = types.size() == 1
                    ? recordRepository.findAllByOperationTypeAndUserAndDeletedFalse(types.get(0), user, paging)
                    : recordRepository.findAllByOperationTypeInAndUserAndDeletedFalse(types, user, paging);
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
