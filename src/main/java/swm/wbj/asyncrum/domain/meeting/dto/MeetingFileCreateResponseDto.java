package swm.wbj.asyncrum.domain.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeetingFileCreateResponseDto {

    private String preSignedURL;
}
