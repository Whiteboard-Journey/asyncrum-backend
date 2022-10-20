package swm.wbj.asyncrum.domain.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.team.entity.Team;
import swm.wbj.asyncrum.domain.member.dto.MemberReadResponseDto;
import swm.wbj.asyncrum.domain.teammember.entity.TeamMember;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class TeamReadResponseDto {

    private Long id;
    private String name;
    private String code;
    private String pictureUrl;
    private List<MemberReadResponseDto> members;

    private Set<String> openMeetings;

    public TeamReadResponseDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.code = team.getCode();
        this.pictureUrl = team.getProfileImageUrl();
        this.members = team.getMembers().stream()
                .map(TeamMember::getMember)
                .map(MemberReadResponseDto::new)
                .collect(Collectors.toList());
        this.openMeetings = team.getOpenMeeting();
    }
}
