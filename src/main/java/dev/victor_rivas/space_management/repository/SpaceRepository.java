package dev.victor_rivas.space_management.repository;

import dev.victor_rivas.space_management.enums.SpaceStatus;
import dev.victor_rivas.space_management.enums.SpaceType;
import dev.victor_rivas.space_management.model.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    Optional<Space> findByCode(String code);
    List<Space> findByType(SpaceType type);
    List<Space> findByStatus(SpaceStatus status);
    List<Space> findByBuilding(String building);
    boolean existsByCode(String code);

    @Query("SELECT s FROM Space s WHERE s.type = :type AND s.status = :status")
    List<Space> findByTypeAndStatus(@Param("type") SpaceType type,
                                    @Param("status") SpaceStatus status);
}