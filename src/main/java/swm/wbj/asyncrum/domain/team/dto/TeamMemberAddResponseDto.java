package swm.wbj.asyncrum.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberAddResponseDto {

    private Long teamId;
    private Long memberId;
}
