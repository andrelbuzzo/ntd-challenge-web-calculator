package com.ntd.webcalculator.record;

import com.ntd.webcalculator.enums.OperationType;
import com.ntd.webcalculator.exception.InvalidOperationException;
import com.ntd.webcalculator.operation.OperationService;
import com.ntd.webcalculator.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final OperationService operationService;
    private final RecordRepository recordRepository;

    public Page<RecordResponse> filter(String search, Collection<String> operationTypeValues, User user, Pageable paging) {
        Page<Record> records;
        List<String> types = normalizeOperationTypes(operationTypeValues);
        if (search != null && !search.isBlank() && !types.isEmpty()) {
            records = recordRepository.searchByUserAndOperationTypes(user, search.trim(), types, paging);
        } else if (search != null && !search.isBlank()) {
            records = recordRepository.searchByUser(user, search.trim(), paging);
        } else if (!types.isEmpty()) {
            records = recordRepository.findAllByOperationTypeInAndUserAndDeletedFalse(types, user, paging);
        } else {
            records = recordRepository.findAllByUserAndDeletedFalse(user, paging);
        }
        return records.map(RecordResponse::from);
    }

    private List<String> normalizeOperationTypes(Collection<String> operationTypeValues) {
        if (operationTypeValues == null) {
            return List.of();
        }

        return operationTypeValues.stream()
                .filter(value -> value != null)
                .flatMap(value -> Arrays.stream(value.split(",")))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(this::normalizeOperationType)
                .distinct()
                .toList();
    }

    private String normalizeOperationType(String value) {
        try {
            return OperationType.valueOf(value.toUpperCase(Locale.ROOT))
                    .name()
                    .toLowerCase(Locale.ROOT);
        } catch (IllegalArgumentException exception) {
            throw new InvalidOperationException("Unsupported operation type: " + value);
        }
    }

    @Transactional
    public void save(String operator, User user, BigDecimal leftOperand,
                     BigDecimal rightOperand, BigDecimal balanceBefore,
                     BigDecimal balanceAfter, String finalResult) {
        OperationType operationType = operationService.checkOperation(operator);
        Record record = new Record(operationType.name(), leftOperand, operator, rightOperand,
                operationType.cost(), user, balanceBefore, balanceAfter, finalResult);
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
