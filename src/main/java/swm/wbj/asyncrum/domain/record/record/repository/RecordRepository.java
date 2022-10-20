package swm.wbj.asyncrum.domain.record.record.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.record.record.entity.Record;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.team.entity.Team;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("SELECT r FROM Record r WHERE r.team = :team" +
            " AND (r.scope = swm.wbj.asyncrum.global.type.ScopeType.TEAM OR r.member = :member)")
    Page<Record> findAllByTeam(
            @Param("team") Team team,
            @Param("member") Member member,
            Pageable pageable
    );

    @Query("SELECT r FROM Record r WHERE r.team = :team" +
            " AND (r.scope = swm.wbj.asyncrum.global.type.ScopeType.TEAM OR r.member = :member)" +
            " AND r.id <= :topId")
    Page<Record> findAllByTeamAndTopId(
            @Param("team") Team team,
            @Param("member") Member member,
            @Param("topId") Long topId,
            Pageable pageable
    );

    Page<Record> findAllByTeamAndMember(Team team, Member member, Pageable pageable);

    @Query("SELECT r FROM Record r WHERE r.team = :team AND r.member = :member AND r.id <= :topId")
    Page<Record> findAllByTeamAndMemberAndTopId(
            @Param("team") Team team,
            @Param("member") Member member,
            @Param("topId") Long topId,
            Pageable pageable
    );
}
