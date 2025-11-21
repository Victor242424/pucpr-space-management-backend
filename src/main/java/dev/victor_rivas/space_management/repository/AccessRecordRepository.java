package dev.victor_rivas.space_management.repository;

import dev.victor_rivas.space_management.enums.AccessStatus;
import dev.victor_rivas.space_management.model.entity.AccessRecord;
import dev.victor_rivas.space_management.model.entity.Space;
import dev.victor_rivas.space_management.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRecordRepository extends JpaRepository<AccessRecord, Long> {

    List<AccessRecord> findByStudent(Student student);
    List<AccessRecord> findBySpace(Space space);
    List<AccessRecord> findByStatus(AccessStatus status);
    boolean existsBySpaceId(Long spaceId);
    boolean existsByStudentId(Long studentId);

    @Query("SELECT ar FROM AccessRecord ar WHERE ar.student = :student AND ar.status = :status")
    List<AccessRecord> findByStudentAndStatus(@Param("student") Student student,
                                              @Param("status") AccessStatus status);

    @Query("SELECT ar FROM AccessRecord ar WHERE ar.space = :space AND ar.status = :status")
    List<AccessRecord> findBySpaceAndStatus(@Param("space") Space space,
                                            @Param("status") AccessStatus status);

    @Query("SELECT ar FROM AccessRecord ar WHERE ar.student = :student " +
            "AND ar.space = :space AND ar.status = 'ACTIVE'")
    Optional<AccessRecord> findActiveAccessByStudentAndSpace(@Param("student") Student student,
                                                             @Param("space") Space space);

    @Query("SELECT COUNT(ar) FROM AccessRecord ar WHERE ar.space = :space " +
            "AND ar.status = 'ACTIVE'")
    Long countActiveAccessBySpace(@Param("space") Space space);

    @Query("SELECT ar FROM AccessRecord ar WHERE ar.entryTime BETWEEN :startDate AND :endDate")
    List<AccessRecord> findByEntryTimeBetween(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ar FROM AccessRecord ar WHERE ar.space = :space " +
            "AND ar.entryTime BETWEEN :startDate AND :endDate")
    List<AccessRecord> findBySpaceAndEntryTimeBetween(@Param("space") Space space,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);
}