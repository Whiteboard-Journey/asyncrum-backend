package swm.wbj.asyncrum.domain.userteam.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageCreateResponseDto {
    private Long id;
    private String preSignedURL;
}