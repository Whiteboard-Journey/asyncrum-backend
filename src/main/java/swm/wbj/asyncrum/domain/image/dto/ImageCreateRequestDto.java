package swm.wbj.asyncrum.domain.image.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.image.entity.Image;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.type.ScopeType;

@Data
@Getter
@NoArgsConstructor
public class ImageCreateRequestDto {

    private String title;
    private String description;
    private String scope;

    public Image toEntity(Member author){
        return Image.createImage()
                .title(title)
                .description(description)
                .scope(ScopeType.of(scope))
                .author(author)
                .build();
    }
}
