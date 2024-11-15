package com.ntd.webcalculator.record;

import com.ntd.webcalculator.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID> {

    Page<Record> findAll(Pageable pageable);

    Page<Record> findAllByOperationType(String operationType, Pageable pageable);

    Page<Record> findAllByUser(User user, Pageable pageable);

    Page<Record> findAllByOperationTypeAndUser(String operationType, User user, Pageable pageable);

}