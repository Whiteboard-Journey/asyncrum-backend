package swm.wbj.asyncrum.domain.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.team.entity.Team;

@Data
public class TeamLightReadDto {

    private Long id;
    private String name;
    private String code;
    private String pictureUrl;

    public TeamLightReadDto(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.code = team.getCode();
        this.pictureUrl = team.getProfileImageUrl();
    }
}
