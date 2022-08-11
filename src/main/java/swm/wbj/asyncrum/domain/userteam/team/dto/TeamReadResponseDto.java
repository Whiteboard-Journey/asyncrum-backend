package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;

import java.util.List;

@Data
public class TeamReadResponseDto {

    private Long id;
    private String name;
    private String code;
    private String pictureUrl;
    private List<Member> members;

    public TeamReadResponseDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.code = team.getCode();
        this.pictureUrl = team.getProfileImageUrl();
        this.members = team.getMembers();
    }

}
