package swm.wbj.asyncrum.domain.record.entity;

import lombok.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "record")
public class Record {

    @Id
    @Column(name = "record_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String videoUrl;

    private String title;

    private String description;

    private String scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void update( String title, String description, String scope){
        this.title = title;
        this.description = description;
        this.scope = scope;
    }
}
