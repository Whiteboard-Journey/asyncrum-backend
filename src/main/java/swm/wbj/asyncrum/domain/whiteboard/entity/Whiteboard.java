package swm.wbj.asyncrum.domain.whiteboard.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;

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
    private String title;

    @Column
    private String description;

    @Column
    private String scope;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    private Member author;

    @Builder(builderMethodName = "createWhiteboard")
    public Whiteboard(String title, String description, String scope, Member author) {
        this.title = title;
        this.description = description;
        this.scope = scope;
        this.author = author;
    }

    public void update(String title, String description, String whiteboardFileKey, String scope) {
        if(title != null ) this.title = title;
        if(description != null ) this.description = description;
        if(whiteboardFileKey != null ) this.whiteboardFileKey = whiteboardFileKey;
        if(scope != null ) this.scope = scope;
    }
}
