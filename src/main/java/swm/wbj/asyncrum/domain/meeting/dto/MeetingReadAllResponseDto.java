package swm.wbj.asyncrum.domain.meeting.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.meeting.entity.Meeting;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MeetingReadAllResponseDto {

    private List<MeetingReadResponseDto> meetings;

    public MeetingReadAllResponseDto(List<Meeting> meetings){
        this.meetings = meetings.stream()
                .map(MeetingReadResponseDto::new)
                .collect(Collectors.toList());
    }

}
