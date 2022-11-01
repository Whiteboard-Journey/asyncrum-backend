package swm.wbj.asyncrum.domain.record.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.record.bookmark.entity.Bookmark;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(targetEntity = Bookmark.class, fetch = FetchType.LAZY)
    private Bookmark bookmark;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @JsonIgnore
    @Column
    private String commentFileKey;
    @Column
    private String commentFileUrl;
    @Column
    private String author;
    @Column
    private String description;

    @Size(max = 512)
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @Builder(builderMethodName = "createComment")
    public Comment(Bookmark bookmark,
                   Member member,
                   String author,
                   String description
                   ){
        this.bookmark = bookmark;
        this.member = member;
        this.author = author;
        this.description = description;
    }

    public void updateComment(String description){
        if(description != null) this.description = description;
    }

    public void updateProfileImage(String profileImageUrl) {
        if(profileImageUrl != null ) this.profileImageUrl = profileImageUrl;
    }

    public void updateCommentFileMetadata(String commentFileKey, String commentFileUrl) {
        if(commentFileKey != null) this.commentFileKey = commentFileKey;
        if(commentFileUrl != null) this.commentFileUrl = commentFileUrl;
    }


}
