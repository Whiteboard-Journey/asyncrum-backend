package swm.wbj.asyncrum.domain.userteam.team.entity;

import lombok.*;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "team")
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String code;

    @Column
    private String pictureUrl;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private List<Member> members = new ArrayList<>();

    @Builder
    public Team(String name, String code, String pictureUrl) {
        this.name = name;
        this.code = code;
        this.pictureUrl = pictureUrl;
    }

    public void update(String name, String pictureUrl) {
        this.name = name;
        this.pictureUrl = pictureUrl;
    }

    public void addMember(Member member) {
        member.updateTeam(this);
        this.members.add(member);
    }

    public void removeMember(Member member) {
        this.members.remove(member);
        member.updateTeam(null);
    }

}
