package dev.victor_rivas.space_management.repository;

import dev.victor_rivas.space_management.enums.Role;
import dev.victor_rivas.space_management.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByStudentId(Long studentId);

    void deleteByStudentId(Long studentId);

    List<User> findByRole(Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByStudentId(Long studentId);
}
