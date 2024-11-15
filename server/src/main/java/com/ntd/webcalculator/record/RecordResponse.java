package com.ntd.webcalculator.record;

import com.ntd.webcalculator.user.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RecordResponse {

    private UUID id;

    private String operationType;

    private BigDecimal cost;

    private User user;

    private String operationResponse;

    boolean deleted;

}