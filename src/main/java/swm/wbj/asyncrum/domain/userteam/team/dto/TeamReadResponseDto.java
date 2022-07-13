package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;

@Data
public class TeamReadResponseDto {
    private String name;
    private String code;
    private String pictureUrl;

    public TeamReadResponseDto(Team team) {
        this.name = team.getName();
        this.code = team.getCode();
        this.pictureUrl = team.getPictureUrl();
    }
}
