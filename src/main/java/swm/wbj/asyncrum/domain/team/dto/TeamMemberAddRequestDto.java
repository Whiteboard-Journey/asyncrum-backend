package swm.wbj.asyncrum.domain.team.dto;

import lombok.Data;

@Data
public class TeamMemberAddRequestDto {

    private Long memberId;
    private String memberEmail;
}
