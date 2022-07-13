package swm.wbj.asyncrum.domain.userteam.team.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Page<Team> findAll(Pageable pageable);
    @Query(
            value = "SELECT * FROM team AS t WHERE t.id <= :topId",
            countQuery = "SELECT COUNT(*) FROM team AS t WHERE t.id <= :topId",
            nativeQuery = true
    )
    Page<Team> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );
    Boolean existsByCode(String code);
}
