package com.ntd.webcalculator.record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecordResponse(
        UUID id,
        String operationType,
        BigDecimal amount,
        BigDecimal cost,
        BigDecimal userBalance,
        String operationResponse,
        LocalDateTime createdAt
) {
    public static RecordResponse from(Record record) {
        return new RecordResponse(
                record.getId(),
                record.getOperationType(),
                record.getAmount(),
                record.getCost(),
                record.getUserBalance(),
                record.getOperationResponse(),
                record.getCreatedAt()
        );
    }
}
