package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

@Data
public class WhiteboardCreateRequestDto {

    private String title;
    private String description;
    private String scope;

    public Whiteboard toEntity(Member author) {
        return Whiteboard.createWhiteboard()
                .title(title)
                .description(description)
                .scope(scope)
                .author(author)
                .build();
    }
}
