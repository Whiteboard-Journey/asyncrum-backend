package swm.wbj.asyncrum.domain.record.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.thymeleaf.expression.Sets;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.entity.BaseEntity;
import swm.wbj.asyncrum.global.type.ScopeType;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
//@ToString
@Builder
public class Record extends BaseEntity{

    @Id
    @Column(name = "record_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    @JsonIgnore
    private String recordFileKey;

    @Column
    private String recordFileUrl;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ScopeType scope;


    @ElementCollection
    @CollectionTable(name="listOfSeenUsers")
    @Column(name="SEEN_NAME")
    private Set<Long> seenMemberIdGroup = new HashSet<>();


//    @JsonIgnore
    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    private Member author;

    @Builder(builderMethodName = "createRecord")
    public Record(String title, String description, ScopeType scope, Member author, Set<Long> seenMemberIdGroup ) {
        this.title = title;
        this.description = description;
        this.scope = scope;
        this.author = author;
        this.seenMemberIdGroup = seenMemberIdGroup;

    }

    public void update( String title, String description, String recordFileKey, String recordFileUrl, ScopeType scope, Set<Long> seenMemberIdGroup){
        if(title != null) this.title = title;
        if(description != null) this.description = description;
        if(recordFileKey != null) this.recordFileKey = recordFileKey;
        if(recordFileUrl != null) this.recordFileUrl = recordFileUrl;
        if (scope != null) this.scope = scope;
        if (seenMemberIdGroup != null) this.seenMemberIdGroup = seenMemberIdGroup;
    }
}
