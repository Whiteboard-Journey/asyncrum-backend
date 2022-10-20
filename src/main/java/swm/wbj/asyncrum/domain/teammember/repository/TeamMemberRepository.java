package swm.wbj.asyncrum.domain.teammember.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.teammember.entity.TeamMember;

import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByTeamAndMember(Team team, Member currentMember);

    Page<TeamMember> findAllByMember(Member currentMember, Pageable pageable);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.member = :member AND tm.id <= :topId")
    Page<TeamMember> findAllByMemberWithTopId(
            @Param("member") Member member,
            @Param("topId") Long topId,
            Pageable pageable);
}
