package swm.wbj.asyncrum.domain.image.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.image.entity.Image;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Page<Image> findAll(Pageable pageable);

    @Query(
            value = "SELECT * FROM image AS i WHERE i.image_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM image AS i WHERE i.image_id <= :topId",
            nativeQuery = true
    )
    Page<Image> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM image AS i WHERE (i.member_id in (select member_id from member as m where m.team_id = :teamId) AND (i.scope = \"TEAM\")) OR (i.member_id = :memberId)",
            countQuery = "SELECT COUNT(*) FROM image AS i WHERE (i.member_id in (select member_id from member as m where m.team_id = :teamId) AND (i.scope = \"TEAM\")) OR (i.member_id = :memberId)",
            nativeQuery = true
    )
    Page<Image> findAllByTeam(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM image AS i WHERE (i.member_id in (select member_id from member as m where m.team_id = :teamId) AND (i.scope = \"TEAM\")) OR (i.member_id = :memberId) AND (i.image_id <= :topId)",
            countQuery = "SELECT COUNT(*) FROM image AS i WHERE (i.member_id in (select member_id from member as m where m.team_id = :teamId) AND (i.scope = \"TEAM\")) OR (i.member_id = :memberId) AND (i.image_id <= :topId)",
            nativeQuery = true
    )
    Page<Image> findAllByTeamAndTopId(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM image AS i WHERE i.member_id = :memberId",
            countQuery = "SELECT COUNT(*) FROM image AS i WHERE i.member_id = :memberId",
            nativeQuery = true
    )
    Page<Image> findAllByAuthor(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM image AS i WHERE i.member_id = :memberId AND i.image_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM image AS i WHERE i.member_id = :memberId AND i.image_id <= :topId",
            nativeQuery = true
    )
    Page<Image> findAllByAuthorAndTopId(
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );

    Boolean existsByTitle(String title);
}
