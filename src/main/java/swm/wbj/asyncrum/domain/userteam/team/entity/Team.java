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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "team_id")
    Long id;

    @Column
    String name;

    @Column(unique = true)
    String code;

    @Column
    String pictureUrl;

    @OneToMany(mappedBy = "team")
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
        this.members.add(member);
    }

}
