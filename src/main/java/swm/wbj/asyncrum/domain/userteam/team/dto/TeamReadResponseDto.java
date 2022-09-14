package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.member.dto.MemberReadResponseDto;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.domain.userteam.teammember.entity.TeamMember;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TeamReadResponseDto {

    private Long id;
    private String name;
    private String code;
    private String pictureUrl;
    private List<MemberReadResponseDto> members;

    public TeamReadResponseDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.code = team.getCode();
        this.pictureUrl = team.getProfileImageUrl();
        this.members = team.getMembers().stream()
                .map(TeamMember::getMember)
                .map(MemberReadResponseDto::new)
                .collect(Collectors.toList());
    }
}
