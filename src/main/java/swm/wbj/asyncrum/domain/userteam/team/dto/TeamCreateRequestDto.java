package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;

@Data
public class TeamCreateRequestDto {

    private String name;
    private String code;

    public Team toEntity() {
        return Team.builder()
                .name(name)
                .code(code)
                .build();
    }
}
