package swm.wbj.asyncrum.domain.userteam.team.dto;

import lombok.Data;
import swm.wbj.asyncrum.domain.userteam.team.entity.Team;
import swm.wbj.asyncrum.global.utils.UiAvatarService;

@Data
public class TeamCreateRequestDto {

    private String name;
    private String code;

    public Team toEntity() {
        return Team.builder()
                .name(name)
                .code(code)
                .profileImageUrl(UiAvatarService.getProfileImageUrl(name))
                .build();
    }
}
