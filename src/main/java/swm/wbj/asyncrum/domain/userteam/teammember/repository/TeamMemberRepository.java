package swm.wbj.asyncrum.domain.userteam.teammember.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.userteam.teammember.entity.TeamMember;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByTeamAndMember(Team team, Member currentMember);

    Page<TeamMember> findAllByMember(Member currentMember, Pageable pageable);

    @Query(
            value = "SELECT * FROM team_member AS tm WHERE tm.member_id = :memberId AND tm.id <= :topId",
            countQuery = "SELECT COUNT(*) FROM team_member AS tm  WHERE tm.member_id = :memberId AND tm.id <= :topId",
            nativeQuery = true
    )
    Page<TeamMember> findAllByMemberAndTopId(
            @Param("memberId") Long memberId,
            @Param("topId") Long topId,
            Pageable pageable);
}
