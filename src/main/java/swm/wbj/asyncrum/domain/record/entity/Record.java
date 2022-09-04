package swm.wbj.asyncrum.domain.record.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "record")
@Entity
public class Record extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column
    @Enumerated(EnumType.STRING)
    private ScopeType scope;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    private Member author;

    @Builder(builderMethodName = "createRecord")
    public Record(String title, String description, ScopeType scope, Member author) {
        this.title = title;
        this.description = description;
        this.scope = scope;
        this.author = author;
    }

    public void updateTitleAndDescription(String title, String description) {
        if(title != null) this.title = title;
        if(description != null) this.description = description;
    }

    public void updateRecordFileMetadata(String recordFileKey, String recordFileUrl) {
        if(recordFileKey != null) this.recordFileKey = recordFileKey;
        if(recordFileUrl != null) this.recordFileUrl = recordFileUrl;
    }

    public void updateScope(ScopeType scope) {
        if (scope != null) this.scope = scope;
    }
}
