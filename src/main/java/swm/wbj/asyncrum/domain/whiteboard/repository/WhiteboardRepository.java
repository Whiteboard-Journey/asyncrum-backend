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

    Page<Whiteboard> findAllByTeam(Team team, Pageable pageable);

    @Query("SELECT w FROM Whiteboard w" +
            " WHERE w.team = :team" +
            " AND w.id <= :topId")
    Page<Whiteboard> findAllByTeamAndTopIdJQPL(
            @Param("team") Team team,
            @Param("topId") Long topId,
            Pageable pageable
    );

    Page<Whiteboard> findAllByTeamAndMember(Team currentTeam, Member currentMember, Pageable pageable);

    @Query("SELECT w FROM Whiteboard w" +
            " WHERE w.team = :team" +
            " AND w.member = :member" +
            " AND w.id <= :topId"
    )
    Page<Whiteboard> findAllByTeamAndMemberAndTopIdJQPL(
            @Param("team") Team team,
            @Param("member") Member member,
            @Param("topId") Long topId,
            Pageable pageable
    );
}
