package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WhiteboardUpdateResponseDto {

    private Long id;
    private String preSignedURL;
}
