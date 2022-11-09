package swm.wbj.asyncrum.domain.team.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swm.wbj.asyncrum.domain.team.meeting.entity.Meeting;
import swm.wbj.asyncrum.domain.team.entity.Team;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByTeam(Team team);

}
