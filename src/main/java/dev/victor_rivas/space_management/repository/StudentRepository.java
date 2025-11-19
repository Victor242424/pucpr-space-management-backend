package dev.victor_rivas.space_management.repository;

import dev.victor_rivas.space_management.enums.StudentStatus;
import dev.victor_rivas.space_management.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegistrationNumber(String registrationNumber);
    Optional<Student> findByEmail(String email);
    List<Student> findByStatus(StudentStatus status);
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByEmail(String email);
}
