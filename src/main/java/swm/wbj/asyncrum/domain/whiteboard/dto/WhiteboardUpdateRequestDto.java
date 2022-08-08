package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import swm.wbj.asyncrum.global.type.ScopeType;

@Data
public class WhiteboardUpdateRequestDto {

    private String title;
    private String description;
    private String scope;
}
