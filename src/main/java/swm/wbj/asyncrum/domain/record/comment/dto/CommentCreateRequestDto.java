package swm.wbj.asyncrum.domain.record.comment.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.domain.record.comment.entity.Comment;


@Data
@Getter
@NoArgsConstructor
public class CommentCreateRequestDto {

    private String author;
    private String description;

    private Long bookmarkId;


    public Comment toEntity(Bookmark bookmark, Member member){
        return Comment.createComment()
                .bookmark(bookmark)
                .member(member)
                .author(author)
                .description(description)
                .build();
    }
}
