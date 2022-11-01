package swm.wbj.asyncrum.domain.record.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCreateResponseDto {

    private Long id;

    private String preSignedURL;
}
