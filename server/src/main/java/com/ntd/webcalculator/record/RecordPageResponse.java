package com.ntd.webcalculator.record;

import org.springframework.data.domain.Page;

import java.util.List;

public record RecordPageResponse(
        List<RecordResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static RecordPageResponse from(Page<RecordResponse> records) {
        return new RecordPageResponse(
                records.getContent(),
                records.getNumber(),
                records.getSize(),
                records.getTotalElements(),
                records.getTotalPages(),
                records.isFirst(),
                records.isLast()
        );
    }
}
