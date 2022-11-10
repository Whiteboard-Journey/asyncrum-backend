package swm.wbj.asyncrum.domain.meeting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeetingCreateResponseDto {

    private Long id;

    private String preSignedURL;

}
