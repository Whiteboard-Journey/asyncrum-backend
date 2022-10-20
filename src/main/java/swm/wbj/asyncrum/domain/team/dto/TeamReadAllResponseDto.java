package swm.wbj.asyncrum.domain.team.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;
import swm.wbj.asyncrum.domain.teammember.entity.TeamMember;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TeamReadAllResponseDto {

    private List<TeamLightReadDto> teams;
    private Pageable pageable;
    private Boolean isList;

    public TeamReadAllResponseDto(List<TeamMember> teamList, Pageable pageable, Boolean isList) {
        this.teams = teamList.stream()
                .map(TeamMember::getTeam)
                .map(TeamLightReadDto::new)
                .collect(Collectors.toList());
        this.pageable = pageable;
        this.isList = isList;
    }
}
