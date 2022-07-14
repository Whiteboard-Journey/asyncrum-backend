package swm.wbj.asyncrum.domain.userteam.member.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import swm.wbj.asyncrum.domain.record.entity.Record;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String email;

    private String pictureUrl;

    private String phone;

    private String nickname;


    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Record> records = new ArrayList<>();

    public void update( String phone, String nickname){
        this.phone = phone;
        this.nickname = nickname;
    }
}
