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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final OperationService operationService;
    private final RecordRepository recordRepository;

    public List<Record> filter(String operationType, User userId, Pageable paging) {
        Page<Record> records;

        if (operationType != null) {
            if (userId != null) {
                records = recordRepository.findAllByOperationTypeAndUser(operationType, userId, paging);
            } else {
                records = recordRepository.findAllByOperationType(operationType, paging);
            }
        } else if (userId != null) {
            records = recordRepository.findAllByUser(userId, paging);
        } else {
            records = recordRepository.findAll(paging);
        }

        return records.getContent();
    }

    @Transactional
    public void save(String operator, User user, BigDecimal credits, String finalResult) {
        OperationType operationType = operationService.checkOperation(operator);
        Record record = new Record(operationType.name(), operationType.cost(), user, credits,
                finalResult);
        recordRepository.save(record);
    }

    @Transactional
    public boolean delete(UUID id, boolean delete) {
        Optional<Record> record = recordRepository.findById(id);
        if (record.isPresent()) {
            Record entity = record.get();
            entity.deleted = delete;
            recordRepository.save(entity);
            return true;
        }
        return false;
    }

}
