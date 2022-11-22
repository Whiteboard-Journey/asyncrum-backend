package swm.wbj.asyncrum.domain.meeting.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.meeting.entity.Meeting;
import swm.wbj.asyncrum.domain.team.entity.Team;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByTeam(Team team);

    List<Meeting> findAllByTeamOrderByIdDesc(Team team);


}
