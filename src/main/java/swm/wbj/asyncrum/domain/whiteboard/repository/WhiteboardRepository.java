package swm.wbj.asyncrum.domain.whiteboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

@Repository
public interface WhiteboardRepository extends JpaRepository<Whiteboard, Long> {

    Page<Whiteboard> findAll(Pageable pageable);

    @Query(
            value = "SELECT * FROM whiteboard AS w WHERE w.id <= :topId",
            countQuery = "SELECT COUNT(*) FROM whiteboard AS w WHERE w.id <= :topId",
            nativeQuery = true
    )
    Page<Whiteboard> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );

    Boolean existsByTitle(String title);
}
