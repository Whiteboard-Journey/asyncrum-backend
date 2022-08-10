package swm.wbj.asyncrum.domain.image.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;

@Data
public class ImageUpdateRequestDto {
    private String title;
    private String description;
    private String scope;
    private String imageFileKey;
    private String imageFileUrl;
}
