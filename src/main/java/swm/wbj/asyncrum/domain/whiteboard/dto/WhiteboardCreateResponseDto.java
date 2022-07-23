package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WhiteboardCreateResponseDto {

    private Long id;
    private String preSignedURL;
}
