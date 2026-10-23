package com.ntd.webcalculator.record;

import com.ntd.webcalculator.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Collection;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID> {

    Page<Record> findAllByDeletedFalse(Pageable pageable);

    Page<Record> findAllByOperationTypeAndDeletedFalse(String operationType, Pageable pageable);

    Page<Record> findAllByUserAndDeletedFalse(User user, Pageable pageable);

    Page<Record> findAllByOperationTypeAndUserAndDeletedFalse(String operationType, User user, Pageable pageable);

    @Query("""
            select record from Record record
            where record.user = :user and record.deleted = false
              and lower(record.operationType) in :operationTypes
            """)
    Page<Record> findAllByOperationTypeInAndUserAndDeletedFalse(
            @Param("operationTypes") Collection<String> operationTypes,
            @Param("user") User user,
            Pageable pageable);

    @Query("""
            select record from Record record
            where record.user = :user and record.deleted = false
              and (lower(record.operationType) like lower(concat('%', :search, '%'))
                   or lower(record.operationResponse) like lower(concat('%', :search, '%')))
            """)
    Page<Record> searchByUser(@Param("user") User user, @Param("search") String search, Pageable pageable);

    @Query("""
            select record from Record record
            where record.user = :user and record.deleted = false
              and lower(record.operationType) in :operationTypes
              and (lower(record.operationType) like lower(concat('%', :search, '%'))
                   or lower(record.operationResponse) like lower(concat('%', :search, '%')))
            """)
    Page<Record> searchByUserAndOperationTypes(@Param("user") User user,
                                                @Param("search") String search,
                                                @Param("operationTypes") Collection<String> operationTypes,
                                                Pageable pageable);

    java.util.Optional<Record> findByIdAndUser(UUID id, User user);

}
