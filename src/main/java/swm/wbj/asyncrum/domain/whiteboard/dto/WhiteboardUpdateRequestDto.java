package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;

@Data
public class WhiteboardUpdateRequestDto {

    private String title;
    private String description;
    private String scope;
}
