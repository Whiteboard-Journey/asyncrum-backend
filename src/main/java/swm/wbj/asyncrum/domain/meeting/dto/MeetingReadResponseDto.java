package swm.wbj.asyncrum.domain.meeting.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.meeting.entity.Meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class MeetingReadResponseDto {

    private Long id;

    private String meetingName;

    private Long teamId;

    private String meetingFileUrl;

    private Boolean status;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private List<String> participants;

    public MeetingReadResponseDto(Meeting meeting){
        this.id = meeting.getId();
        this.meetingName = meeting.getMeetingName();
        this.teamId = meeting.getTeam().getId();
        this.meetingFileUrl = meeting.getMeetingFileUrl();
        this.status = meeting.getStatus();
        this.createdDate = meeting.getCreatedDate();
        this.lastModifiedDate = meeting.getLastModifiedDate();
        this.participants = meeting.getParticipants();
    }




}
