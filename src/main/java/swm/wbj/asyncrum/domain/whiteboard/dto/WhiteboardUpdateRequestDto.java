package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.validation.constraints.NotNull;

@Data
public class WhiteboardUpdateRequestDto {

    private String title;
    private String description;
    private String scope;
}
