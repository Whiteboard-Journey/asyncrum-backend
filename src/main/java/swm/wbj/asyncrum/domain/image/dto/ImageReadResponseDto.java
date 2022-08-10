package swm.wbj.asyncrum.domain.image.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.image.entity.Image;
import swm.wbj.asyncrum.global.type.ScopeType;

@Data
public class ImageReadResponseDto {

    private String title;
    private String description;
    private String imageUrl;
    private ScopeType scope;

    public ImageReadResponseDto(Image image){
        this.title = image.getTitle();
        this.description = image.getDescription();
        this.imageUrl = image.getImageFileUrl();
        this.scope = image.getScope();
    }
}
