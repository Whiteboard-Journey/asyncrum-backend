package swm.wbj.asyncrum.domain.whiteboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity
public class Whiteboard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "whiteboard_id")
    private Long id;

    @Column
    private String whiteboardFileKey;

    @Column
    private String whiteboardFileUrl;

    @Column
    @NotNull
    private String title;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ScopeType scope;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    private Member author;

    @Builder(builderMethodName = "createWhiteboard")
    public Whiteboard(String title, String description, ScopeType scope, Member author) {
        this.title = title;
        this.description = description;
        this.scope = scope;
        this.author = author;
    }

    public void updateTitleAndDescription(String title, String description) {
        if(title != null) this.title = title;
        if(description != null) this.description = description;
    }

    public void updateWhiteboardFileMetadata(String whiteboardFileKey, String whiteboardFileUrl) {
        if(whiteboardFileKey != null) this.whiteboardFileKey = whiteboardFileKey;
        if(whiteboardFileUrl != null) this.whiteboardFileUrl = whiteboardFileUrl;
    }

    public void updateScope(ScopeType scope) {
        if (scope != null) this.scope = scope;
    }
}
