package swm.wbj.asyncrum.domain.record.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "record")
@Entity
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @JsonIgnore
    @Column
    private String recordFileKey;

    @Column
    private String recordFileUrl;

    @Column(length = 50)
    private String title;

    @Column(length = 200)
    private String description;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String projectMetadata;

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

    @Builder(builderMethodName = "createRecord")
    public Record(String title, String description, ScopeType scope, Member member, Team team) {
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

    public void updateRecordFileMetadata(String recordFileKey, String recordFileUrl) {
        if(recordFileKey != null) this.recordFileKey = recordFileKey;
        if(recordFileUrl != null) this.recordFileUrl = recordFileUrl;
    }

    public void updateRecordProjectMetadata(String projectMetadata) {
        if(projectMetadata != null) this.projectMetadata = projectMetadata;
    }

    public void updateScope(ScopeType scope) {
        if (scope != null) this.scope = scope;
    }
}
