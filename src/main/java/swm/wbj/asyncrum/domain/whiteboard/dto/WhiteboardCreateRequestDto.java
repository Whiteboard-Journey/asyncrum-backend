package swm.wbj.asyncrum.domain.whiteboard.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.whiteboard.entity.Whiteboard;

@Data
public class WhiteboardCreateRequestDto {

    private String whiteboardUrl;
    private String title;
    private String description;
    private String scope;

    // TODO: 로그인 구현 후 JWT를 통해 작성자 객체를 가져온 후 추가
    public Whiteboard toEntity() {
        return Whiteboard.builder()
                .whiteboardUrl(whiteboardUrl)
                .title(title)
                .description(description)
                .scope(scope)
                //.author(author)
                .build();
    }
}
