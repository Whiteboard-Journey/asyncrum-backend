package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.team.dao.Team;

@Data
public class TeamCreateRequestDto {
    private String name;
    private String code;
    private String pictureUrl;

    public Team toEntity() {
        return Team.builder()
                .name(name)
                .code(code)
                .pictureUrl(pictureUrl)
                .build();
    }
}
