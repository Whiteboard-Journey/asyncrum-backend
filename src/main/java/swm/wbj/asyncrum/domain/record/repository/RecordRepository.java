package swm.wbj.asyncrum.domain.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;

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

    Page<Record> findAllByTeam(Team team, Pageable pageable);

    @Query(
            value = "SELECT * FROM record AS r WHERE (r.team_id = :teamId) AND (r.scope = \"TEAM\")) OR (r.member_id = :memberId) AND (r.record_id <= :topId)",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE (r.team_id = :teamId) AND (r.scope = \"TEAM\")) OR (r.member_id = :memberId) AND (r.record_id <= :topId)",
            nativeQuery = true
    )
    Page<Record> findAllByTeamAndTopId(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );

    Page<Record> findAllByTeamAndMember(Team currentTeam, Member currentMember, Pageable pageable);

    @Query(
            value = "SELECT * FROM record AS r WHERE r.team_id = :teamId AND r.member_id = :memberId AND r.record_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM record AS r WHERE r.team_id = :teamId AND r.member_id = :memberId AND r.record_id <= :topId",
            nativeQuery = true
    )
    Page<Record> findAllByTeamAndMemberAndTopId(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );
}
