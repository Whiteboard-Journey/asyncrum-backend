package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

@Data
public class WhiteboardReadResponseDto {

    private String whiteboardUrl;
    private String title;
    private String description;
    private String scope;

    public WhiteboardReadResponseDto(Whiteboard whiteboard) {
        this.whiteboardUrl = whiteboard.getWhiteboardFileKey();
        this.title = whiteboard.getTitle();
        this.description = whiteboard.getDescription();
        this.scope = whiteboard.getScope();
    }

}
