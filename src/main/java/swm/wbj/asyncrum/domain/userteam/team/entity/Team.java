package swm.wbj.asyncrum.domain.userteam.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import swm.wbj.asyncrum.domain.userteam.teammember.entity.TeamMember;
import swm.wbj.asyncrum.global.entity.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Table(name = "team")
@Entity
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String code;

    @JsonIgnore
    @Column
    private String profileImageFileKey;

    @Column
    private String profileImageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TeamMember> members = new ArrayList<>();

    @Builder
    public Team(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public void updateName(String name) {
        if(name != null) this.name = name;
    }

    public void updateProfileImage(String profileImageFileKey, String profileImageUrl) {
        if(profileImageFileKey != null) this.profileImageFileKey = profileImageFileKey;
        if(profileImageUrl != null ) this.profileImageUrl = profileImageUrl;
    }

    public void addMember(TeamMember member) {
        this.members.add(member);
    }

    public void removeMember(TeamMember member) {
        this.members.remove(member);
    }
}
