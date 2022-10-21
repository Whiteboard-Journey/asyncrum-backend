package swm.wbj.asyncrum.domain.whiteboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "whiteboard")
@Entity
public class Whiteboard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "whiteboard_id")
    private Long id;

    @JsonIgnore
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
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne(targetEntity = Team.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Builder(builderMethodName = "createWhiteboard")
    public Whiteboard(String title, String description, ScopeType scope, Member member, Team team) {
        this.title = title;
        this.description = description;
        this.scope = scope;
        this.member = member;
        this.team = team;
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
