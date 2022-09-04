package swm.wbj.asyncrum.domain.userteam.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String profileImageFileKey;

    @Column
    private String profileImageUrl;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private List<Member> members = new ArrayList<>();

    @Builder
    public Team(String name, String code) {

        this.name = name;
        this.code = code;

    }

    public void update(String name, String profileImageFileKey, String profileImageUrl) {

        this.name = name;
        if(profileImageFileKey != null) this.profileImageFileKey = profileImageFileKey;
        if(profileImageUrl != null) this.profileImageUrl = profileImageUrl;

    }

    public void addMember(Member member) {

        member.changeTeam(this);
        this.members.add(member);
    }

    public void removeMember(Member member) {

        member.leaveTeam();
        this.members.remove(member);
    }

}
