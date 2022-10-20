package swm.wbj.asyncrum.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamImageCreateResponseDto {

    private Long id;
    private String preSignedURL;
    private String imageUrl;
}
