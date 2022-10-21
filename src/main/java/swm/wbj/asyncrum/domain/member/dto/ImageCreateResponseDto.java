package swm.wbj.asyncrum.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ImageCreateResponseDto {

    private Long id;
    private String preSignedURL;
    private String imageUrl;
}
