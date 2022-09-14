package swm.wbj.asyncrum.domain.whiteboard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

import java.util.List;

@Repository
public interface WhiteboardRepository extends JpaRepository<Whiteboard, Long> {

    List<Whiteboard> findAll();

    @Query(
            value = "SELECT * FROM whiteboard AS w WHERE w.whiteboard_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM whiteboard AS w WHERE w.whiteboard_id <= :topId",
            nativeQuery = true
    )
    Page<Whiteboard> findAllByTopId(
            @Param("topId") Long topId,
            Pageable pageable
    );

    Page<Whiteboard> findAllByTeam(Team team, Pageable pageable);

    @Query(
            value = "SELECT * FROM whiteboard AS w WHERE (w.team_id = :teamId) AND (w.scope = \"TEAM\")) OR (w.member_id = :memberId) AND (w.whiteboard_id <= :topId)",
            countQuery = "SELECT COUNT(*) FROM whiteboard AS w WHERE (w.team_id = :teamId) AND (w.scope = \"TEAM\")) OR (w.member_id = :memberId) AND (w.whiteboard_id <= :topId)",
            nativeQuery = true
    )
    Page<Whiteboard> findAllByTeamAndTopId(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );

    Page<Whiteboard> findAllByTeamAndMember(Team currentTeam, Member currentMember, Pageable pageable);

    @Query(
            value = "SELECT * FROM whiteboard AS w WHERE w.team_id = :teamId AND w.member_id = :memberId AND w.whiteboard_id <= :topId",
            countQuery = "SELECT COUNT(*) FROM whiteboard AS w WHERE w.team_id = :teamId AND w.member_id = :memberId AND w.whiteboard_id <= :topId",
            nativeQuery = true
    )
    Page<Whiteboard> findAllByTeamAndMemberAndTopId(
            @Param("teamId") Long teamId,
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable
    );
}
