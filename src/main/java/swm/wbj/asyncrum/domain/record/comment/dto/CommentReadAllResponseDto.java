package swm.wbj.asyncrum.domain.record.comment.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.comment.entity.Comment;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentReadAllResponseDto {

    private List<CommentReadResponseDto> comments;

    public CommentReadAllResponseDto(List<Comment> comments){
        this.comments = comments.stream()
                .map(CommentReadResponseDto::new)
                .collect(Collectors.toList());
    }
}
