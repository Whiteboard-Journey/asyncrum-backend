package swm.wbj.asyncrum.domain.meeting.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.meeting.entity.Meeting;
import swm.wbj.asyncrum.domain.team.entity.Team;
@Data
@Getter
@NoArgsConstructor
public class MeetingCreateRequestDto {

    private Long teamId;
    private String meetingName;

    public Meeting toEntity(Team team){
        return Meeting.createMeeting()
                .team(team)
                .meetingName(meetingName)
                .build();
    }


}
