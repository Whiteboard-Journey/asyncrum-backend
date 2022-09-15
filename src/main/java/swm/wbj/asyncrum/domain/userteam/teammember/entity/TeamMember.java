package swm.wbj.asyncrum.domain.userteam.teammember.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.type.TeamRoleType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_member")
@Entity
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "team_role_type", length = 20)
    private TeamRoleType teamRoleType;

    @Builder(builderMethodName = "createTeamMember")
    public TeamMember(Team team, Member member, TeamRoleType teamRoleType) {
        setTeam(team);
        setMember(member);
        this.teamRoleType = teamRoleType;
    }

    private void setMember(Member member) {
        this.member = member;
        member.addTeam(this);
    }

    private void setTeam(Team team) {
        this.team = team;
        team.addMember(this);
    }
}
