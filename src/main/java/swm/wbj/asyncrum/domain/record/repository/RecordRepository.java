package swm.wbj.asyncrum.domain.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findAll();

    @Query(
            value = "SELECT * FROM record AS r WHERE r.record_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.record_id <= :topId",
            nativeQuery = true
    )
    Page<Record> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM record AS r WHERE (r.member_id in (select member_id from member as m where m.team_id = :teamId) AND (r.scope = \"TEAM\")) OR (r.member_id = :memberId)",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE (r.member_id in (select member_id from member as m where m.team_id = :teamId) AND (r.scope = \"TEAM\")) OR (r.member_id = :memberId)",
            nativeQuery = true
    )
    Page<Record> findAllByTeam(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM record AS r WHERE (r.member_id in (select member_id from member as m where m.team_id = :teamId) AND (r.scope = \"TEAM\")) OR (r.member_id = :memberId) AND (r.record_id <= :topId)",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE (r.member_id in (select member_id from member as m where m.team_id = :teamId) AND (r.scope = \"TEAM\")) OR (r.member_id = :memberId) AND (r.record_id <= :topId)",
            nativeQuery = true
    )
    Page<Record> findAllByTeamAndTopId(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );




    @Query(
            value = "SELECT * FROM record AS r WHERE r.member_id = :memberId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.member_id = :memberId",
            nativeQuery = true
    )
    Page<Record> findAllByAuthor(
            @Param("memberId") Long memberId,
            Pageable pageable
    );


    @Query(
            value = "SELECT * FROM record AS r WHERE r.member_id = :memberId AND r.record_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.member_id = :memberId AND r.record_id <= :topId",
            nativeQuery = true
    )
    Page<Record> findAllByAuthorAndTopId(
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );



    Boolean existsByTitle(String title);
}
