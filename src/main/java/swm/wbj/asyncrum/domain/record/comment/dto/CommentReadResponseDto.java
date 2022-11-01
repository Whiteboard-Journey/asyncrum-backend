package swm.wbj.asyncrum.domain.record.comment.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.record.comment.entity.Comment;

import java.time.LocalDateTime;
@Data
public class CommentReadResponseDto {

    private Long id;

    private String description;

    private Long bookmarkId;

    private String commentFileUrl;

    private String author;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private String profileImageUrl;

    public CommentReadResponseDto(Comment comment){
        this.id = comment.getId();
        this.bookmarkId = comment.getBookmark().getId();
        this.author = comment.getAuthor();
        this.description = comment.getDescription();
        this.commentFileUrl = comment.getCommentFileUrl();
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.profileImageUrl = comment.getProfileImageUrl();
    }
}
